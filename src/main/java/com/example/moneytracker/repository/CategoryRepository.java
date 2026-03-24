package com.example.moneytracker.repository;

import com.example.moneytracker.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    //select * from tb_categories where profile_id
    List<CategoryEntity> findByProfileId(Long ProfileId);
    //select * from tb_categories where profile_id and id
   Optional<CategoryEntity> findByIdAndProfileId(Long id, Long ProfileId);
    //select * from tb_categories where profile_id and type
    List<CategoryEntity>findByTypeAndProfileId(String type, Long  profileId);

    boolean existsByNameAndProfileId(String name, Long profileId );
}
