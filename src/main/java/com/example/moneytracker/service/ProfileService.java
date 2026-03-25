package com.example.moneytracker.service;

import com.example.moneytracker.dto.AuthDto;
import com.example.moneytracker.dto.ProfileDto;
import com.example.moneytracker.entity.ProfileEntity;

import java.util.Map;

public interface ProfileService {
    public ProfileDto createProfile(ProfileDto profileDto);
    public boolean activateProfile(String activationToken);

    Map<String, Object> authenticateAndGenerateToken(AuthDto authDto);
    ProfileEntity getCurrentProfile();
}
