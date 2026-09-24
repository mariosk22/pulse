package com.pulse.backend.repository;
import com.pulse.backend.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SportRepository extends JpaRepository<Sport,Long> {
}
