package com.example.moneytracker.repository;


import com.example.moneytracker.entity.ExpenseEntity;
import com.example.moneytracker.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity,Long> {

    // where profile_id order by date desc ;
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);
    // where profile_id order by date desc limit 5;
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long ProfileId);
    @Query("select sum(i.amount) from IncomeEntity i Where i.profile.id = :profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long ProfileId);
    //select * from tbl_income where profile_id =? and date between ? and ? and name like %?%
    List<IncomeEntity>findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort

    );
    // select * from Tbl_income where profile_id =? and date between ? and ?;
    List<IncomeEntity> findByProfileIdAndDateBetween(Long ProfileId, LocalDate startDate, LocalDate endDate);
    List<IncomeEntity>findByProfileIdAndDate(Long ProfileId, LocalDate date);
}
