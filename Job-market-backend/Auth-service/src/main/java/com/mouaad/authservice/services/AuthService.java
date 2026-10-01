package com.mouaad.authservice.services;

import com.mouaad.authservice.clients.UserServiceClient;
import com.mouaad.authservice.dtos.CreateUserProfileRequest;
import com.mouaad.authservice.dtos.AuthResponse;
import com.mouaad.authservice.dtos.LoginRequest;
import com.mouaad.authservice.dtos.RegisterRequest;
import com.mouaad.authservice.entities.AuthUser;
import com.mouaad.authservice.entities.Role;
import com.mouaad.authservice.repositories.AuthUserRepository;
import com.mouaad.authservice.security.CustomUserDetails;
import com.mouaad.authservice.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceClient userServiceClient;


    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        //save Auth credentials
        AuthUser user = new AuthUser(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.valueOf(request.getRole().toUpperCase())
        );
        AuthUser savedUser = authUserRepository.save(user);
        //call user service to create the profile data
        userServiceClient.createProfile(new CreateUserProfileRequest(savedUser.getId(),request.getFullName()));

        // 3. Generate initial token so they are logged in immediately
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String token = jwtTokenProvider.generateToken(userDetails);

        return new AuthResponse(token, savedUser.getId());
    }

    public AuthResponse login(LoginRequest request) {
        // This triggers CustomUserDetailsService and checks the password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);
    
        return new AuthResponse(token, userDetails.getAuthUser().getId());
    }
}
