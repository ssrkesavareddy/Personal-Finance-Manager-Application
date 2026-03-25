package com.example.moneytracker.service;

import com.example.moneytracker.dto.CategoryDto;

import java.util.List;

public interface CategoryService  {

    public CategoryDto saveCategory(CategoryDto categoryDto);
    public List<CategoryDto> getCategoriesForCurrentUser();
    public List<CategoryDto> getCategoriesByTypeForCurrentUser(String type);
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);
}
