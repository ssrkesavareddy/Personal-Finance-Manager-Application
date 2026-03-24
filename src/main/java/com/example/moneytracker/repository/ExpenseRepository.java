package com.example.moneytracker.repository;

import com.example.moneytracker.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity,Long> {
    // where profile_id order by date desc ;
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);
   // where profile_id order by date desc limit 5;
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long ProfileId);
    @Query("select sum(e.amount) from ExpenseEntity e Where e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long ProfileId);
    //select * from tbl_expenses where profile_id =? and date between ? and ? and name like %?%
    List<ExpenseEntity>findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort

    );
    // select * from Tbl_expense where profile_id =? and date between ? and ?;
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long ProfileId, LocalDate startDate, LocalDate endDate);
    //tb_expenses where profile_id and date
    List<ExpenseEntity>findByProfileIdAndDate(Long ProfileId,LocalDate date);


}
