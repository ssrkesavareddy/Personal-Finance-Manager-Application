package com.example.moneytracker.controller;



import com.example.moneytracker.dto.AuthDto;
import com.example.moneytracker.dto.ProfileDto;
import com.example.moneytracker.service.ProfileService;
import com.example.moneytracker.service.impl.ProfileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto) {
        ProfileDto registeredProfile = profileService.createProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
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
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDto authDto) {
        try {
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

//    @GetMapping("/test")
//    public String test() {
//        return  "test sucessfully";
//    }
}