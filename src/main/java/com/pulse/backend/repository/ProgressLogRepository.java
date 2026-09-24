package com.pulse.backend.repository;
import com.pulse.backend.entity.ProgressLog;
import com.pulse.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressLogRepository extends JpaRepository<ProgressLog,Long> {
List<ProgressLog> findByUserOrderByLogDateDesc(User user);
}
