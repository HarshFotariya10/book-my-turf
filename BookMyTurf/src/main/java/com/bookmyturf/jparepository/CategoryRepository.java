package com.bookmyturf.jparepository;

import com.bookmyturf.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT DISTINCT c FROM Category c WHERE LOWER(c.location.city) = LOWER(:city)")
    List<Category> findDistinctByCityIgnoreCase(@Param("city") String city);


}
