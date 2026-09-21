package com.pulse.backend.dto.user;
import com.pulse.backend.entity.User;
import com.pulse.backend.entity.enums.Gender;
import com.pulse.backend.entity.enums.Goal;
import com.pulse.backend.entity.enums.Level;
import lombok.Getter;
@Getter
public class UserResponse {
    private final Long id;
    private final String email;
    private final String fullName;
    private final Gender gender;
    private final Integer age;
    private final Double heightCm;
    private final Double weightKg;
    private final String sportName;
    private final Level level;
    private final Goal goal;

    public UserResponse(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.gender =  user.getGender();
        this.age = user.getAge();
        this.heightCm = user.getHeightCm();
        this.weightKg = user. getWeightKg();
        this.sportName = user.getSport() !=null?
                user.getSport().getName() : null;
        this.level = user.getLevel();
        this.goal = user.getGoal();
    }

}
