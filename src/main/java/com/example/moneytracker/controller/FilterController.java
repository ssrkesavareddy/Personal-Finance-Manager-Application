package com.example.moneytracker.controller;


import com.example.moneytracker.dto.FilterDto;
import com.example.moneytracker.dto.IncomeDto;
import com.example.moneytracker.service.ExpenseService;
import com.example.moneytracker.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransaction(@RequestBody FilterDto filter) {

        LocalDate startDate = filter.getStartDate() != null
                ? filter.getStartDate()
                : LocalDate.of(1970, 1, 1);

        LocalDate endDate = filter.getEndDate() != null
                ? filter.getEndDate()
                : LocalDate.now();

        String sortField = filter.getSortField() != null
                ? filter.getSortField()
                : "date";

        Sort.Direction direction =
                "desc".equalsIgnoreCase(filter.getSortOrder())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        String keyword = filter.getKeyword() != null
                ? filter.getKeyword()
                : "";

        Sort sort = Sort.by(direction, sortField);

        if ("income".equalsIgnoreCase(filter.getType())) {
            List<IncomeDto> incomes =
                    incomeService.filterIncome(startDate, endDate, keyword, sort);

            return ResponseEntity.ok(incomes);
        } else if("expense".equalsIgnoreCase(filter.getType())) {
            return ResponseEntity.ok(
                    expenseService.filterExpenses(startDate, endDate, keyword, sort)
            );
        }
        else{
            return ResponseEntity.badRequest().body("Invaild type must be income or expense");
        }
    }
}
