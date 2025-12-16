package com.bookmyturf.jparepository;

import com.bookmyturf.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByAdminId(Long adminId);

    @Query("SELECT COUNT(l.id) FROM Location l WHERE l.admin.id = :adminId")
    Long countLocationsByAdmin(Long adminId);
}
