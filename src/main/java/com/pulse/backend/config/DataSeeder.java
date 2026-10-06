package com.pulse.backend.config;

import com.pulse.backend.entity.Exercise;
import com.pulse.backend.entity.Sport;
import com.pulse.backend.entity.enums.ExerciseType;
import com.pulse.backend.entity.enums.Level;
import com.pulse.backend.repository.ExerciseRepository;
import com.pulse.backend.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.pulse.backend.entity.enums.ExerciseType.CARDIO;
import static com.pulse.backend.entity.enums.ExerciseType.STRENGTH;
import static com.pulse.backend.entity.enums.Level.BEGINNER;
import static com.pulse.backend.entity.enums.Level.INTERMEDIATE;
import static com.pulse.backend.entity.enums.Level.PRO;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SportRepository sportRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    public void run(String... args) {
        if (sportRepository.count() == 0) {
            seedSports();
        }
        if (exerciseRepository.count() == 0) {
            seedExercises();
        }
    }

    private void seedSports() {
        sportRepository.saveAll(List.of(
                sport("Running", "Running training from recreational jogging to races"),
                sport("Bodybuilding", "Strength training focused on muscle growth"),
                sport("Football", "Football conditioning: endurance, speed and strength"),
                sport("Swimming", "Pool swimming training"),
                sport("Cycling", "Road or mountain bike training"),
                sport("Basketball", "Basketball conditioning: explosiveness and agility")));
        log.info("Seeded sports catalog");
    }

    private void seedExercises() {
        List<Sport> sports = sportRepository.findAll();
        List<Exercise> catalog = new ArrayList<>();

        catalog.add(exercise("Bodyweight squat", "Basic squat without added load.", "Legs", STRENGTH, BEGINNER, null));
        catalog.add(exercise("Push-ups", "Classic push-ups with the body in a straight line.", "Chest", STRENGTH, BEGINNER, null));
        catalog.add(exercise("Lunges", "Alternating forward lunges.", "Legs", STRENGTH, BEGINNER, null));
        catalog.add(exercise("Glute bridge", "Hip raise while lying on the back.", "Glutes", STRENGTH, BEGINNER, null));
        catalog.add(exercise("Plank", "Holding the body in a forearm support.", "Core", STRENGTH, BEGINNER, null));
        catalog.add(exercise("Superman", "Raising arms and legs while lying face down.", "Back", STRENGTH, BEGINNER, null));
        catalog.add(exercise("Barbell squat", "Squat with a barbell on the shoulders.", "Legs", STRENGTH, INTERMEDIATE, null));
        catalog.add(exercise("Pull-ups", "Overhand pull-ups on a bar.", "Back", STRENGTH, INTERMEDIATE, null));
        catalog.add(exercise("Dips", "Dips on parallel bars or a bench.", "Triceps", STRENGTH, INTERMEDIATE, null));
        catalog.add(exercise("Romanian deadlift", "Deadlift with slightly bent knees.", "Hamstrings", STRENGTH, INTERMEDIATE, null));
        catalog.add(exercise("Deadlift", "Basic heavy pull from the floor.", "Back", STRENGTH, PRO, null));
        catalog.add(exercise("Front squat", "Squat with the barbell resting on the front of the shoulders.", "Legs", STRENGTH, PRO, null));
        catalog.add(exercise("Overhead press", "Standing barbell press overhead.", "Shoulders", STRENGTH, PRO, null));

        catalog.add(exercise("Jump rope", "Continuous jumping over a rope.", "Full body", CARDIO, BEGINNER, null));
        catalog.add(exercise("Jumping jacks", "Jumps spreading the arms and legs.", "Full body", CARDIO, BEGINNER, null));
        catalog.add(exercise("Mountain climbers", "Fast alternating knee drives in a plank position.", "Core", CARDIO, BEGINNER, null));
        catalog.add(exercise("High knees", "Running in place with high knee lifts.", "Legs", CARDIO, BEGINNER, null));
        catalog.add(exercise("Brisk uphill walking", "Fast walking on an incline.", "Legs", CARDIO, BEGINNER, null));
        catalog.add(exercise("Burpees", "Squat, push-up position, jump.", "Full body", CARDIO, INTERMEDIATE, null));
        catalog.add(exercise("30/30 interval sprints", "30 seconds of sprinting, 30 seconds of easy jogging.", "Legs", CARDIO, INTERMEDIATE, null));
        catalog.add(exercise("Box jumps", "Explosive jumps onto a raised platform.", "Legs", CARDIO, INTERMEDIATE, null));
        catalog.add(exercise("Tabata circuit", "8 rounds of 20 seconds of work and 10 seconds of rest.", "Full body", CARDIO, PRO, null));
        catalog.add(exercise("Hill sprints", "Short maximal sprints uphill.", "Legs", CARDIO, PRO, null));

        Sport running = find(sports, "Running");
        catalog.add(exercise("Steady run", "Running at an even pace.", "Legs", CARDIO, BEGINNER, running));
        catalog.add(exercise("Long slow run", "A long run at low intensity.", "Legs", CARDIO, BEGINNER, running));
        catalog.add(exercise("Interval run", "Alternating fast and slow segments.", "Legs", CARDIO, INTERMEDIATE, running));
        catalog.add(exercise("Tempo run", "A run at a pace just below race pace.", "Legs", CARDIO, INTERMEDIATE, running));
        catalog.add(exercise("Hill intervals", "Repeated uphill runs.", "Legs", CARDIO, PRO, running));
        catalog.add(exercise("Calf raises", "Rising onto the toes from a standing position.", "Calves", STRENGTH, BEGINNER, running));
        catalog.add(exercise("Bulgarian split squat", "Single-leg squat with the rear foot on a bench.", "Legs", STRENGTH, INTERMEDIATE, running));

        Sport bodybuilding = find(sports, "Bodybuilding");
        catalog.add(exercise("Biceps curl", "Dumbbell curl.", "Biceps", STRENGTH, BEGINNER, bodybuilding));
        catalog.add(exercise("Triceps pushdown", "Cable pushdown or lying triceps extension.", "Triceps", STRENGTH, BEGINNER, bodybuilding));
        catalog.add(exercise("Lateral raises", "Raising dumbbells to the sides.", "Shoulders", STRENGTH, BEGINNER, bodybuilding));
        catalog.add(exercise("Bench press", "Barbell press on a flat bench.", "Chest", STRENGTH, INTERMEDIATE, bodybuilding));
        catalog.add(exercise("Barbell row", "Bent-over pull of the barbell to the abdomen.", "Back", STRENGTH, INTERMEDIATE, bodybuilding));
        catalog.add(exercise("Leg press", "Pressing a weight away with the legs on a machine.", "Legs", STRENGTH, INTERMEDIATE, bodybuilding));
        catalog.add(exercise("Weighted pull-ups", "Pull-ups with added weight.", "Back", STRENGTH, PRO, bodybuilding));

        Sport football = find(sports, "Football");
        catalog.add(exercise("Dribbling run", "Running with the ball at the foot.", "Legs", CARDIO, BEGINNER, football));
        catalog.add(exercise("Agility ladder", "Fast foot patterns through a coordination ladder.", "Legs", CARDIO, BEGINNER, football));
        catalog.add(exercise("Sprints with turns", "Sprinting with changes of direction.", "Legs", CARDIO, INTERMEDIATE, football));
        catalog.add(exercise("Shuttle run", "Repeated runs between two markers.", "Legs", CARDIO, INTERMEDIATE, football));
        catalog.add(exercise("Single-leg squat", "Pistol squat with support.", "Legs", STRENGTH, INTERMEDIATE, football));
        catalog.add(exercise("Plyometric jumps", "Explosive jumps over hurdles.", "Legs", STRENGTH, INTERMEDIATE, football));

        Sport swimming = find(sports, "Swimming");
        catalog.add(exercise("Freestyle swimming", "Front crawl at an even pace.", "Full body", CARDIO, BEGINNER, swimming));
        catalog.add(exercise("Breaststroke swimming", "Swimming with the breaststroke technique.", "Full body", CARDIO, BEGINNER, swimming));
        catalog.add(exercise("50 m swim intervals", "Alternating fast and slow 50 m segments.", "Full body", CARDIO, INTERMEDIATE, swimming));
        catalog.add(exercise("Long swim sets", "Continuous swimming of 800 m or more.", "Full body", CARDIO, PRO, swimming));
        catalog.add(exercise("Resistance band pulls", "Imitating the swim stroke with a resistance band.", "Back", STRENGTH, BEGINNER, swimming));
        catalog.add(exercise("Dumbbell pullover", "Moving a dumbbell behind the head while lying down.", "Chest", STRENGTH, INTERMEDIATE, swimming));

        Sport cycling = find(sports, "Cycling");
        catalog.add(exercise("Steady ride", "Riding at a steady intensity.", "Legs", CARDIO, BEGINNER, cycling));
        catalog.add(exercise("Interval ride", "Alternating high and low intensity.", "Legs", CARDIO, INTERMEDIATE, cycling));
        catalog.add(exercise("Hill climbing", "Long climbs, seated and standing.", "Legs", CARDIO, INTERMEDIATE, cycling));
        catalog.add(exercise("Sprint intervals", "Short maximal sprints on the bike.", "Legs", CARDIO, PRO, cycling));
        catalog.add(exercise("Weighted calf raises", "Rising onto the toes while holding dumbbells.", "Calves", STRENGTH, BEGINNER, cycling));
        catalog.add(exercise("Weighted lunges", "Lunges while holding dumbbells.", "Legs", STRENGTH, INTERMEDIATE, cycling));

        Sport basketball = find(sports, "Basketball");
        catalog.add(exercise("Dribbling in motion", "Dribbling while running around the court.", "Legs", CARDIO, BEGINNER, basketball));
        catalog.add(exercise("Court sprints", "Repeated sprints from basket to basket.", "Legs", CARDIO, INTERMEDIATE, basketball));
        catalog.add(exercise("Squat jumps", "An explosive jump out of a squat.", "Legs", STRENGTH, INTERMEDIATE, basketball));
        catalog.add(exercise("Rim jumps", "Repeated jumps toward the rim.", "Legs", STRENGTH, INTERMEDIATE, basketball));

        exerciseRepository.saveAll(catalog);
        log.info("Seeded exercise catalog ({} exercises)", catalog.size());
    }

    private Sport sport(String name, String description) {
        return Sport.builder().name(name).description(description).build();
    }

    private Sport find(List<Sport> sports, String name) {
        return sports.stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Sport missing from catalog: " + name));
    }

    private Exercise exercise(String name, String description, String muscleGroup,
                              ExerciseType type, Level minLevel, Sport sport) {
        return Exercise.builder()
                .name(name)
                .description(description)
                .muscleGroup(muscleGroup)
                .type(type)
                .minLevel(minLevel)
                .sport(sport)
                .build();
    }
}
