package com.pulse.backend.service;
import com.pulse.backend.dto.auth.AuthResponse;
import com.pulse.backend.dto.auth.LoginRequest;
import com.pulse.backend.dto.auth.RegisterRequest;
import com.pulse.backend.entity.enums.Role;
import com.pulse.backend.exception.ApiException;
import com.pulse.backend.repository.UserRepository;
import com.pulse.backend.security.JwtService;
import com.pulse.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.pulse.backend.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AuthService {
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
private final JwtService jwtService;
private final AuthenticationManager authenticationManager;

public AuthResponse register(RegisterRequest request){
    if (userRepository.existsByEmail(request.getEmail())){
        throw new ApiException(HttpStatus.CONFLICT,"An account with this email already exists");
    }
    User user = User.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .role(Role.USER)
            .build();
    userRepository.save(user);
    String token = jwtService.generateToken(new UserPrincipal(user));
    return new AuthResponse(token,user.getId(),user.getEmail(),user.getFullName());

}
public AuthResponse login(LoginRequest request){
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
    );

    User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));


    String  token =jwtService.generateToken(new UserPrincipal(user));
    return new AuthResponse(token,user.getId(),user.getEmail(),user.getFullName());
}
}
