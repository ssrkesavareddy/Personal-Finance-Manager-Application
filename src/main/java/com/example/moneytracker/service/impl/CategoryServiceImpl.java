package com.example.moneytracker.service.impl;

import com.example.moneytracker.dto.CategoryDto;
import com.example.moneytracker.entity.CategoryEntity;
import com.example.moneytracker.entity.ProfileEntity;
import com.example.moneytracker.repository.CategoryRepository;

import com.example.moneytracker.service.CategoryService;
import com.example.moneytracker.service.ExpenseService;
import com.example.moneytracker.service.IncomeService;
import com.example.moneytracker.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;


    //save category
    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {

        ProfileEntity profile = profileService.getCurrentProfile();

        if (categoryRepository.existsByNameAndProfileId(
                categoryDto.getName(),
                profile.getId()
        )) {
            throw new RuntimeException( "Category already exists");
        }

        CategoryEntity category = toEntity(categoryDto, profile);
        category = categoryRepository.save(category);

        return toDto(category);
    }


    public List<CategoryDto> getCategoriesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<CategoryDto> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity>entities= categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return entities.stream()
                .map(this::toDto)
                .toList();

    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        ProfileEntity profile = profileService.getCurrentProfile();
       CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException( "Category not found"));
       existingCategory.setName(categoryDto.getName());
       existingCategory.setIcon(categoryDto.getIcon());

       existingCategory = categoryRepository.save(existingCategory);
       return toDto(existingCategory);


    }


    private CategoryEntity toEntity(CategoryDto categoryDto, ProfileEntity profileEntity) {
        return CategoryEntity.builder()
                .name(categoryDto.getName())
                .icon(categoryDto.getIcon())
                .profile(profileEntity)
                .type(categoryDto.getType())
                .build();
    }

    private CategoryDto toDto(CategoryEntity categoryEntity) {
        return CategoryDto.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfile() != null ? categoryEntity.getProfile().getId(): null)
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .type(categoryEntity.getType())
                .build();
    }
}
