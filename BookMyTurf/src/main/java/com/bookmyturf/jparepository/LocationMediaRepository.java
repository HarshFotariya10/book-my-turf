package com.bookmyturf.jparepository;

import com.bookmyturf.entity.LocationMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LocationMediaRepository extends JpaRepository<LocationMedia, Long> {
}
