package com.pulse.backend.service;
import com.pulse.backend.dto.user.OnboardingRequest;
import com.pulse.backend.dto.user.UserResponse;
import com.pulse.backend.entity.Sport;
import com.pulse.backend.entity.User;
import com.pulse.backend.exception.ApiException;
import com.pulse.backend.repository.SportRepository;
import com.pulse.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final  SportRepository sportRepository;

    public UserResponse getProfile(User user){
        return new UserResponse(user);
    }
    public UserResponse updateOnboarding(User user,OnboardingRequest request){
        Sport sport = sportRepository.findById(request.getSportId()).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Sport does not exist"));

        user.setSport(sport);
        user.setLevel(request.getLevel());
        user.setGoal(request.getGoal());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setHeightCm(request.getHeightCm());
        user.setWeightKg(request.getWeightKg());


        userRepository.save(user);
        return new UserResponse(user);
    }
}
