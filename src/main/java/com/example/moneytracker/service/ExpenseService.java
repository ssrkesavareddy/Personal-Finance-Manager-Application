package com.example.moneytracker.service;

import com.example.moneytracker.dto.ExpenseDto;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
public ExpenseDto addExpense(ExpenseDto dto);
public List<ExpenseDto> getCurrentMonthExpensesForCurrentUser();
public void deleteExpense(Long expenseId);
public List<ExpenseDto> getLatestFiveExpensesForCurrentUser();
public BigDecimal getTotalExpensesForCurrentUser();
public List<ExpenseDto> filterExpenses(LocalDate startDate , LocalDate endDate, String keyword, Sort sort);
public List<ExpenseDto> getExpensesForUserOnDate(Long profileId, LocalDate date);

}
