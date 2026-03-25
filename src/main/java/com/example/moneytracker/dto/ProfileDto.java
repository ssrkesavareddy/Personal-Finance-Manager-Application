package com.example.moneytracker.dto;

import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDto {

    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String profileImgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
