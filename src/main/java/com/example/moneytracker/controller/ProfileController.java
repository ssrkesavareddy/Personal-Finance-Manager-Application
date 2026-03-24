package com.example.moneytracker.controller;



import com.example.moneytracker.dto.AuthDto;
import com.example.moneytracker.dto.ProfileDto;
import com.example.moneytracker.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<?> registerProfile(@RequestBody ProfileDto profileDto) {
        try {
            ProfileDto registeredProfile = profileService.createProfile(profileDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/activation/{token}")
    public ResponseEntity<String> activateProfile(@PathVariable String token) {

        boolean isActivated = profileService.activateProfile(token);

        if (isActivated) {
            return ResponseEntity.ok("Profile activated");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Activation failed or already used");
        }
    }
    @PostMapping("/resend-activation")
public ResponseEntity<String> resendActivation(@RequestParam String email) {

    ProfileEntity user = profileRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getIsActive()) {
        return ResponseEntity.badRequest().body("Account already activated");
    }

    String activationLink = activationUrl + "/activation/" + user.getActivationToken();

    emailService.sendEmail(
            user.getEmail(),
            "Resend Activation",
            "Click to activate: " + activationLink
    );

    return ResponseEntity.ok("Activation email sent");
}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDto authDto) {
        try {
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
