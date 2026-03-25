package com.example.moneytracker.service;

import com.example.moneytracker.dto.ExpenseDto;
import com.example.moneytracker.dto.IncomeDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeService {
    public IncomeDto addIncome( IncomeDto dto);
    public List<IncomeDto>getCurrentMonthIncomeForCurrentUser();
    public void deleteIncome(Long incomeId);
    public List<IncomeDto> getLatestFiveIncomesForCurrentUser();
    public BigDecimal getTotalIncomesForCurrentUser();
    public List<IncomeDto> filterIncome(LocalDate startDate , LocalDate endDate, String keyword, Sort sort);



}
