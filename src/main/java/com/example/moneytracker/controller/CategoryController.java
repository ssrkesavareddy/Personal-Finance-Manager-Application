package com.example.moneytracker.controller;


import com.example.moneytracker.dto.CategoryDto;
import com.example.moneytracker.entity.CategoryEntity;
import com.example.moneytracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
     public ResponseEntity<CategoryDto> saveCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto savedCategory = categoryService.saveCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getACategories() {
        List<CategoryDto> categories = categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(categories);

    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByTypeForCurrentUser(@PathVariable String type) {
        List<CategoryDto> categoryDtoList = categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.ok(categoryDtoList);
    }
     @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto categoryDto) {
       CategoryDto updatedCategory =categoryService.updateCategory(categoryId, categoryDto);
       return ResponseEntity.status(HttpStatus.OK).body(updatedCategory);
     }
}
