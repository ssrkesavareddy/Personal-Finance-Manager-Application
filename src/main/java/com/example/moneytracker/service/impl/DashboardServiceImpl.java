package com.example.moneytracker.service.impl;

import com.example.moneytracker.dto.ExpenseDto;
import com.example.moneytracker.dto.IncomeDto;
import com.example.moneytracker.dto.RecentTransactionDto;
import com.example.moneytracker.entity.ProfileEntity;
import com.example.moneytracker.repository.ExpenseRepository;
import com.example.moneytracker.repository.IncomeRepository;
import com.example.moneytracker.service.DashboardService;
import com.example.moneytracker.service.ExpenseService;
import com.example.moneytracker.service.IncomeService;
import com.example.moneytracker.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProfileService profileService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @Override
    public Map<String, Object> getDashboard() {
        ProfileEntity profile = profileService.getCurrentProfile();



        List<IncomeDto> latestIncome = incomeService.getLatestFiveIncomesForCurrentUser();
        List<ExpenseDto> latestExpense = expenseService.getLatestFiveExpensesForCurrentUser();

        List<RecentTransactionDto> recentTransactions =
                concat(
                        latestIncome.stream().map(income ->
                                RecentTransactionDto.builder()
                                        .id(income.getId())
                                        .profileId(profile.getId())
                                        .icon(income.getIcon())
                                        .name(income.getName())
                                        .date(income.getDate())
                                        .amount(income.getAmount())
                                        .type("income")
                                        .createdAt(income.getCreatedAt())
                                        .updatedAt(income.getUpdatedAt())
                                        .build()
                        ),
                        latestExpense.stream().map(expense ->
                                RecentTransactionDto.builder()
                                        .id(expense.getId())
                                        .profileId(profile.getId())
                                        .icon(expense.getIcon())
                                        .name(expense.getName())
                                        .amount(expense.getAmount())
                                        .date(expense.getDate())
                                        .createdAt(expense.getCreatedAt())
                                        .updatedAt(expense.getUpdatedAt())
                                        .type("expense")
                                        .build()
                        )
                )
                        .sorted((a, b) -> {
                            int cmp = b.getDate().compareTo(a.getDate());
                            if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                                return b.getCreatedAt().compareTo(a.getCreatedAt());
                            }
                            return cmp;
                        })
                        .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalBalance", incomeService.getTotalIncomesForCurrentUser().subtract(expenseService.getTotalExpensesForCurrentUser()));
        response.put("totalIncome", incomeService.getTotalIncomesForCurrentUser());
        response.put("totalExpense", expenseService.getTotalExpensesForCurrentUser());
        response.put("recentFiveExpenses", latestExpense);
        response.put("recentFiveIncome", latestIncome);
        response.put("recentTransactions", recentTransactions);


        return response;
    }
}
