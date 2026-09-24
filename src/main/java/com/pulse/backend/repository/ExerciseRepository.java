package com.pulse.backend.repository;
import com.pulse.backend.entity.Exercise;
import com.pulse.backend.entity.enums.ExerciseType;
import com.pulse.backend.entity.enums.Level;
import jdk.dynalink.linker.LinkerServices;
import lombok.Lombok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise,Long> {
    @Query
            ("""
 select e from Exercise e
          where(e.sport.id = :sportId or e.sport is null)
              and e.type =:type
              and e.minLevel in :allowedLevels""")
    List<Exercise>findAvailable(
            @Param("sportId")Long sportId,
            @Param("type") ExerciseType type,
            @Param("allowedLevels")List<Level> allowedLevels
            );

}
