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
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final  SportRepository sportRepository;

    @Transactional(readOnly = true)
    public UserResponse getProfile(User principalUser){
        return new UserResponse(loadWithSport(principalUser));
    }

    @Transactional
    public UserResponse updateOnboarding(User principalUser,OnboardingRequest request){
        Sport sport = sportRepository.findById(request.getSportId()).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Sport does not exist"));

        // principal.getUser() is an entity loaded in JwtAuthenticationFilter, outside
        // the open-in-view session. The sport would be a detached proxy and getName()
        // would throw a LazyInitializationException, so reload the profile with the sport.
        User user = loadWithSport(principalUser);

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

    private User loadWithSport(User principalUser){
        return userRepository.findWithSportById(principalUser.getId())
                .orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"User does not exist"));
    }
}