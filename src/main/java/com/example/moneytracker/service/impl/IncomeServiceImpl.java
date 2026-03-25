package com.example.moneytracker.service.impl;

import com.example.moneytracker.dto.IncomeDto;
import com.example.moneytracker.entity.CategoryEntity;
import com.example.moneytracker.entity.IncomeEntity;
import com.example.moneytracker.entity.ProfileEntity;
import com.example.moneytracker.enums.CategoryType;
import com.example.moneytracker.repository.CategoryRepository;
import com.example.moneytracker.repository.IncomeRepository;
import com.example.moneytracker.service.IncomeService;
import com.example.moneytracker.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final IncomeRepository incomeRepository;

    // ================= ENTITY → DTO =================
    private IncomeDto toDto(IncomeEntity entity) {
        return IncomeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .amount(entity.getAmount())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .build();
    }

    // ================= DTO → ENTITY =================
    private IncomeEntity toEntity(IncomeDto dto, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .name(dto.getName())
                .category(category)
                .profile(profile)
                .icon(dto.getIcon())
                .date(dto.getDate())
                .amount(dto.getAmount())
                .build();
    }

    // ================= CREATE =================
    @Override
    public IncomeDto addIncome(IncomeDto dto) {

        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required");
        }

        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (category.getType() != CategoryType.INCOME) {
            throw new RuntimeException("Invalid category type: only INCOME categories allowed");
        }

        IncomeEntity newIncome = toEntity(dto, profile, category);
        newIncome = incomeRepository.save(newIncome);

        return toDto(newIncome);
    }

    // ================= GET CURRENT MONTH =================
    @Override
    public List<IncomeDto> getCurrentMonthIncomeForCurrentUser() {

        ProfileEntity profile = profileService.getCurrentProfile();

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        // 🔥 FIXED → JOIN FETCH query
        List<IncomeEntity> list =
                incomeRepository.findByProfile_IdAndDateBetween(
                        profile.getId(),
                        startDate,
                        endDate
                );

        return list.stream()
                .map(this::toDto)
                .toList();
    }

    // ================= DELETE =================
    @Override
    public void deleteIncome(Long incomeId) {

        ProfileEntity profile = profileService.getCurrentProfile();

        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income Not Found"));

        if (!profile.getId().equals(entity.getProfile().getId())) {
            throw new RuntimeException("Unauthorized to delete income");
        }

        incomeRepository.delete(entity);
    }

    // ================= LATEST 5 =================
    @Override
    public List<IncomeDto> getLatestFiveIncomesForCurrentUser() {

        ProfileEntity profile = profileService.getCurrentProfile();

        List<IncomeEntity> list =
                incomeRepository.findTop5ByProfile_IdOrderByDateDesc(profile.getId());

        return list.stream()
                .map(this::toDto)
                .toList();
    }

    // ================= TOTAL =================
    @Override
    public BigDecimal getTotalIncomesForCurrentUser() {

        ProfileEntity profile = profileService.getCurrentProfile();

        BigDecimal total = incomeRepository.findTotalIncomeByProfileId(profile.getId());

        return total != null ? total : BigDecimal.ZERO;
    }

    // ================= FILTER =================
    @Override
    public List<IncomeDto> filterIncome(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {

        ProfileEntity profile = profileService.getCurrentProfile();

        List<IncomeEntity> list =
                incomeRepository.findByProfile_IdAndDateBetweenAndNameContainingIgnoreCase(
                        profile.getId(),
                        startDate,
                        endDate,
                        keyword,
                        sort
                );

        return list.stream()
                .map(this::toDto)
                .toList();
    }
}