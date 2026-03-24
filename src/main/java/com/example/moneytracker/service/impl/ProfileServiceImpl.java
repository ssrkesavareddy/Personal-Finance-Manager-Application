package com.example.moneytracker.service.impl;

import com.example.moneytracker.dto.AuthDto;
import com.example.moneytracker.dto.ProfileDto;
import com.example.moneytracker.entity.ProfileEntity;
import com.example.moneytracker.jwtutil.JwtUtil;
import com.example.moneytracker.service.EmailService;
import com.example.moneytracker.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.moneytracker.repository.ProfileRepository;

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

    ProfileEntity newProfile = toEntity(profileDto);
    newProfile.setActivationToken(UUID.randomUUID().toString());

    newProfile = profileRepository.save(newProfile);

    if (activationUrl == null || activationUrl.isBlank()) {
        throw new RuntimeException("Activation URL not configured");
    }

    String activationLink = activationUrl + "/activation/" + newProfile.getActivationToken();

    String subject = "Account Activation";
    String body = "Click to activate: " + activationLink;

    try {
        emailService.sendEmail(newProfile.getEmail(), subject, body);
    } catch (Exception e) {
        System.out.println("Email failed: " + e.getMessage());
    }

    return toDto(newProfile);
}

    @Override
    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile ->{
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

// separate logic
        if (!isAccountActive(authDto.getEmail())) {
            throw new RuntimeException("Account not activated");
        }

        String token = jwtUtil.generateToken(authDto.getEmail());

        return Map.of(
                "token", token,
                "user", getPublicProfile(authDto.getEmail())
        );
    }

  private ProfileEntity toEntity(ProfileDto dto) {
    return ProfileEntity.builder()
            .fullName(dto.getFullName())
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .profileImgUrl(dto.getProfileImgUrl())
            .isActive(false)   
            .build();
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
    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();

        return profileRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Profile not found with email: " + email +" "+authentication.getName())
                );
    }

    public ProfileDto getPublicProfile(String email){
        ProfileEntity currentUser = null;
        if(email == null){
           currentUser= getCurrentProfile();
        }
        else{
            currentUser = profileRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Profile is not found with email address " + email));
        }
        return ProfileDto.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImgUrl(currentUser.getProfileImgUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }
}
