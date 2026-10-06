package com.pulse.backend.repository;

import com.pulse.backend.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "sport")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
