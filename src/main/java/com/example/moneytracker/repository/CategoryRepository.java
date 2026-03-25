package com.example.moneytracker.repository;

import com.example.moneytracker.entity.CategoryEntity;
import com.example.moneytracker.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByProfileId(Long ProfileId);
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long ProfileId);
    List<CategoryEntity> findByTypeAndProfileId(CategoryType type, Long profileId);
    boolean existsByNameAndProfileId(String name, Long profileId);
}