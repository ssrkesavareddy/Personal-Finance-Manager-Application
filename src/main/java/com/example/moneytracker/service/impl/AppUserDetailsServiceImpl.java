package com.example.moneytracker.service.impl;

import com.example.moneytracker.entity.ProfileEntity;
import com.example.moneytracker.repository.ProfileRepository;
import com.example.moneytracker.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor

public class AppUserDetailsServiceImpl implements AppUserDetailsService {
    public final ProfileRepository profileRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ProfileEntity existingProfile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile Not Found with Email: " + email));
        return User.builder()
                .username(existingProfile.getEmail())
                .password(existingProfile.getPassword())
                .authorities("ROLE_" + existingProfile.getRole())
                .build();
    }
}
