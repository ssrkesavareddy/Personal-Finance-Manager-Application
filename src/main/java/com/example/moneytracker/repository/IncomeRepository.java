package com.example.moneytracker.repository;


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
    @Query("""
SELECT i FROM IncomeEntity i
JOIN FETCH i.category
WHERE i.profile.id = :profileId
ORDER BY i.date DESC
""")
    List<IncomeEntity> findTop5ByProfile_IdOrderByDateDesc(@Param("profileId")Long ProfileId);
    @Query("select sum(i.amount) from IncomeEntity i Where i.profile.id = :profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long ProfileId);
    //select * from tbl_income where profile_id =? and date between ? and ? and name like %?%
    List<IncomeEntity> findByProfile_IdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort

    );
    // select * from Tbl_income where profile_id =? and date between ? and ?;
    @Query("""
SELECT i FROM IncomeEntity i
JOIN FETCH i.category
WHERE i.profile.id = :profileId
AND i.date BETWEEN :startDate AND :endDate
ORDER BY i.date DESC
""")
    List<IncomeEntity> findByProfile_IdAndDateBetween( @Param("profileId") Long profileId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

}
