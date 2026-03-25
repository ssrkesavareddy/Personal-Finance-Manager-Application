package com.example.moneytracker.controller;

import com.example.moneytracker.dto.ExpenseDto;
import com.example.moneytracker.dto.IncomeDto;
import com.example.moneytracker.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")

public class IncomeController {

private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto incomeDto){
       IncomeDto saved = incomeService.addIncome(incomeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);

    }

    @GetMapping
    public ResponseEntity<List<IncomeDto>> getAllIncomes(){
        List<IncomeDto> income = incomeService.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(income);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome (@PathVariable Long id){
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();

    }
}
