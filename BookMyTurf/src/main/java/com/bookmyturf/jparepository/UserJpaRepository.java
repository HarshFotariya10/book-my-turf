package com.bookmyturf.jparepository;

import com.bookmyturf.constraints.Roles;
import com.bookmyturf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRoleAndIsActiveFalse(Roles role);

    boolean existsByEmail(String email);
}
