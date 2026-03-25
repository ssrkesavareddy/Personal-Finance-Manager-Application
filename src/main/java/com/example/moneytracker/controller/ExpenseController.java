package com.example.moneytracker.controller;


import com.example.moneytracker.dto.ExpenseDto;
import com.example.moneytracker.entity.ExpenseEntity;
import com.example.moneytracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;


    @PostMapping
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseDto expenseDto){
        ExpenseDto saved = expenseService.addExpense(expenseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getAllExpenses(){
        List<ExpenseDto> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(expenses);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense (@PathVariable Long id){
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();

    }
}
