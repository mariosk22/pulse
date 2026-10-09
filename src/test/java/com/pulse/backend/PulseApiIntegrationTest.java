package com.pulse.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end smoke test covering the full Angular flow against an in-memory H2
 * database: register, onboard, generate a plan and nutrition, log progress, mark
 * a workout as done and read the plan history. It also checks that
 * unauthenticated calls return the JSON error body produced by
 * JsonAuthenticationEntryPoint.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:pulse;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
class PulseApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullUserJourney() throws Exception {
        String email = "journey@example.com";

        // Register and capture the JWT.
        MvcResult register = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"password123","fullName":"Test User"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNumber())
                .andReturn();
        String token = json(register).get("token").asText();

        // Protected route without a token -> JSON 401 from the entry point.
        mockMvc.perform(get("/api/training-plans"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty());

        // Sports catalogue is public and seeded by DataSeeder.
        MvcResult sports = mockMvc.perform(get("/api/sports").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andReturn();
        long sportId = json(sports).get(0).get("id").asLong();

        // Onboarding fills sport, level and goal.
        mockMvc.perform(put("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sportId":%d,"level":"BEGINNER","goal":"GENERAL_FITNESS",
                                 "gender":"MALE","age":30,"heightCm":180,"weightKg":80}
                                """.formatted(sportId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sportName").isNotEmpty())
                .andExpect(jsonPath("$.level").value("BEGINNER"));

        // Generate the training plan.
        MvcResult generated = mockMvc.perform(post("/api/training-plans/generate")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationWeeks\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.workouts").isArray())
                .andExpect(jsonPath("$.workouts[0].completed").value(false))
                .andExpect(jsonPath("$.workouts[0].exercises[0].exerciseName").isNotEmpty())
                .andReturn();
        long planId = json(generated).get("id").asLong();
        long workoutId = json(generated).get("workouts").get(0).get("id").asLong();

        // History summary is what the Angular plan page reads.
        mockMvc.perform(get("/api/training-plans").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(planId))
                .andExpect(jsonPath("$[0].sportName").isNotEmpty())
                .andExpect(jsonPath("$[0].level").isNotEmpty())
                .andExpect(jsonPath("$[0].goal").isNotEmpty())
                .andExpect(jsonPath("$[0].startDate").isNotEmpty())
                .andExpect(jsonPath("$[0].durationWeeks").value(4))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        // Mark a workout as completed, twice, to prove it is idempotent.
        mockMvc.perform(post("/api/workout-completions")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workoutId\":%d,\"durationMinutes\":45,\"notes\":\"first\"}".formatted(workoutId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workoutId").value(workoutId))
                .andExpect(jsonPath("$.completedAt").isNotEmpty())
                .andExpect(jsonPath("$.durationMinutes").value(45));
        mockMvc.perform(post("/api/workout-completions")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workoutId\":%d,\"durationMinutes\":50}".formatted(workoutId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.durationMinutes").value(50));

        // Only one completion row exists for the workout.
        mockMvc.perform(get("/api/workout-completions").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].completedAt").isNotEmpty());

        // The plan detail now reports the workout as completed.
        mockMvc.perform(get("/api/training-plans/" + planId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workouts[0].id").value(workoutId))
                .andExpect(jsonPath("$.workouts[0].completed").value(true))
                .andExpect(jsonPath("$.workouts[0].completedAt").isNotEmpty())
                .andExpect(jsonPath("$.workouts[0].exercises").isArray());

        // Unmarking removes the completion row.
        mockMvc.perform(delete("/api/workout-completions/" + workoutId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/workout-completions").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // Nutrition plan generated from the profile.
        mockMvc.perform(post("/api/nutrition-plans/generate").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyCalories").isNumber())
                .andExpect(jsonPath("$.meals[0].mealType").isNotEmpty());
        mockMvc.perform(get("/api/nutrition-plans/active").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proteinG").isNumber());

        // Progress logging.
        mockMvc.perform(post("/api/progress")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"weightKg\":80.5,\"bodyFatPct\":18.0,\"notes\":\"first\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weightKg").value(80.5));
        mockMvc.perform(get("/api/progress").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notes").value("first"));

        // Completing a workout that belongs to someone else is a 404.
        mockMvc.perform(post("/api/workout-completions")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workoutId\":999999}"))
                .andExpect(status().isNotFound());
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
