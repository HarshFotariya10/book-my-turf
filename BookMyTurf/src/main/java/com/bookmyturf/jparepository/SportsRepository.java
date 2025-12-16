package com.bookmyturf.jparepository;

import com.bookmyturf.entity.Sports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportsRepository extends JpaRepository<Sports,Long> {

    @Query("SELECT s FROM Sports s " +
            "WHERE LOWER(s.location.city) = LOWER(:city) " +
            "AND LOWER(s.category.name) = LOWER(:categoryName)")
    List<Sports> findByCityAndCategoryIgnoreCase(
            @Param("city") String city,
            @Param("categoryName") String categoryName
    );

    @Query("SELECT COUNT(s.id) FROM Sports s WHERE s.location.admin.id = :adminId")
    Long countSportsByAdmin(Long adminId);
}
