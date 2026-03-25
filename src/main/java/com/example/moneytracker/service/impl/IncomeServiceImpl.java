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

    private IncomeDto toDto(IncomeEntity entity) {
        return IncomeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())

                .amount(entity.getAmount())
                .categoryId(entity.getCategory() !=null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() !=null ? entity.getCategory().getName() : "N/A")
                .build();
    }

    @Override
    public IncomeDto addIncome(IncomeDto dto) {

        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required");
        }

        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        if (category.getType() != CategoryType.INCOME) {
            throw new RuntimeException("Invalid category type: only INCOME categories are allowed for incomes.");
        }

        IncomeEntity newIncome = toEntity(dto, profile, category);
        newIncome = incomeRepository.save(newIncome);

        return toDto(newIncome);
    }

    @Override
    public List<IncomeDto> getCurrentMonthIncomeForCurrentUser() {

            ProfileEntity profile = profileService.getCurrentProfile();
            LocalDate now = LocalDate.now();
            LocalDate startDate= now.withDayOfMonth(1);
            LocalDate endDate= now.withDayOfMonth(now.lengthOfMonth());
            List<IncomeEntity> list= incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
            return list.stream().map(this::toDto).toList();


    }

    @Override
    public void deleteIncome(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(()-> new RuntimeException("income Not Found"));
        if(!profile.getId().equals(entity.getProfile().getId())) {
            throw new RuntimeException("unauthorized to delete income");
        }
        incomeRepository.delete(entity);
    }

    @Override
    public List<IncomeDto> getLatestFiveIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }

    @Override
    public BigDecimal getTotalIncomesForCurrentUser() {
        ProfileEntity Profile = profileService.getCurrentProfile();
        BigDecimal totalExpenses = incomeRepository.findTotalIncomeByProfileId(Profile.getId());
        return totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
    }

    @Override
    public List<IncomeDto> filterIncome(LocalDate startDate, LocalDate endDate, String keyword,Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list =incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase( profile.getId(),
                startDate,
                endDate,
                keyword,
                sort
        );

        return list.stream().map(this::toDto).toList();
    }

    @Override
    public List<IncomeDto> getExpensesForUserOnDate(Long profileId, LocalDate date) {
        return List.of();
    }
}



