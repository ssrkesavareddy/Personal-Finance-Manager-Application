package com.example.moneytracker.service.impl;

import com.example.moneytracker.dto.AuthDto;
import com.example.moneytracker.dto.ProfileDto;
import com.example.moneytracker.entity.ProfileEntity;
import com.example.moneytracker.jwtutil.JwtUtil;
import com.example.moneytracker.repository.ProfileRepository;
import com.example.moneytracker.service.EmailService;
import com.example.moneytracker.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${backendapp.activation.url}")
    private String activationUrl;

    @Override
    public ProfileDto createProfile(ProfileDto profileDto) {

        if (profileRepository.findByEmail(profileDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        ProfileEntity newProfile = ProfileEntity.builder()
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())
                .password(passwordEncoder.encode(profileDto.getPassword()))
                .profileImgUrl(profileDto.getProfileImgUrl())
                .isActive(false)
                .build();

        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        String activationLink = activationUrl + "/activation/" + newProfile.getActivationToken();

        try {
           emailService.sendEmail(
                newProfile.getEmail(),
                "Account Activation",
                "Click to activate: " + activationLink
        );

         } catch (Exception e) {
    System.out.println("Email failed: " + e.getMessage());
    }

       
        return toDto(newProfile);
    }

    @Override
    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authDto.getEmail(),
                            authDto.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!isAccountActive(authDto.getEmail())) {
            throw new RuntimeException(
                    "Account not activated. Please check email or resend activation."
            );
        }

        String token = jwtUtil.generateToken(authDto.getEmail());

        return Map.of(
                "token", token,
                "user", getPublicProfile(authDto.getEmail())
        );
    }

    private boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    private ProfileDto toDto(ProfileEntity entity) {
        return ProfileDto.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .profileImgUrl(entity.getProfileImgUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ProfileDto getPublicProfile(String email) {
        ProfileEntity user = profileRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Profile not found: " + email)
                );
        return toDto(user);
    }
}
