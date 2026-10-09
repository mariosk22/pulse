package com.pulse.backend.repository;
import com.pulse.backend.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User>findByEmail(String email);
    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "sport")
    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmailWithSport(@Param("email") String email);

    @EntityGraph(attributePaths = "sport")
    @Query("select u from User u where u.id = :id")
    Optional<User> findWithSportById(@Param("id") Long id);
}
