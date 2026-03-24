package com.example.moneytracker.service.impl;

import com.example.moneytracker.dto.ExpenseDto;
import com.example.moneytracker.entity.CategoryEntity;
import com.example.moneytracker.entity.ExpenseEntity;
import com.example.moneytracker.entity.ProfileEntity;
import com.example.moneytracker.repository.CategoryRepository;
import com.example.moneytracker.repository.ExpenseRepository;
import com.example.moneytracker.service.CategoryService;
import com.example.moneytracker.service.ExpenseService;
import com.example.moneytracker.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;


import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
 private final CategoryRepository categoryRepository;
 private final ExpenseRepository expenseRepository;
 private final ProfileService profileService;



 private ExpenseEntity toEntity(ExpenseDto dto, ProfileEntity profile, CategoryEntity category) {
     return ExpenseEntity.builder()
             .name(dto.getName())
             .category(category)
             .profile(profile)
             .icon(dto.getIcon())
             .date(dto.getDate())
             .amount(dto.getAmount())
             .build();
 }

    private ExpenseDto toDto(ExpenseEntity entity) {
        return ExpenseDto.builder()
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
    public ExpenseDto addExpense(ExpenseDto dto) {
        if (dto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID is required");
        }
     ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category =categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Category Not Found"));
        ExpenseEntity newExpense = toEntity(dto, profile, category);
        newExpense = expenseRepository.save(newExpense);
        return toDto(newExpense);

    }

    @Override
    public List<ExpenseDto> getCurrentMonthExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate= now.withDayOfMonth(1);
        LocalDate endDate= now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list= expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(this::toDto).toList();

    }

    @Override
    public void deleteExpense(Long expenseId) {

     ProfileEntity profile = profileService.getCurrentProfile();
     ExpenseEntity entity = expenseRepository.findById(expenseId)
             .orElseThrow(()-> new RuntimeException("Expense Not Found"));
     if(!profile.getId().equals(entity.getProfile().getId())) {
         throw new RuntimeException("unauthorized to delete expense");
     }
     expenseRepository.delete(entity);


    }

    @Override
    public List<ExpenseDto> getLatestFiveExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }

    @Override
    public BigDecimal getTotalExpensesForCurrentUser() {
        ProfileEntity previousProfile = profileService.getCurrentProfile();
        BigDecimal totalExpenses = expenseRepository.findTotalExpenseByProfileId(previousProfile.getId());
        return totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
    }

    @Override
    public List<ExpenseDto> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list =expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase( profile.getId(),
                startDate,
                endDate,
                keyword,
                sort
        );
        return list.stream().map(this::toDto).toList();
    }

    @Override
    public List<ExpenseDto> getExpensesForUserOnDate(Long profileId, LocalDate date) {
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDate(profileId,date);
        return list.stream().map(this::toDto).toList();
    }


}
