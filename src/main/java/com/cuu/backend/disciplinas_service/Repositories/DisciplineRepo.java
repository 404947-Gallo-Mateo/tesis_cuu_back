package com.cuu.backend.disciplinas_service.Repositories;

import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisciplineRepo extends JpaRepository<Discipline, UUID> {

    @Query("SELECT d FROM Discipline d WHERE d.name = :name")
    Optional<Discipline> findByName(@Param("name") String name);

    @Query("SELECT d FROM Discipline d JOIN d.teachers t WHERE t.keycloakId = :teacherKeycloakId")
    List<Discipline> findAllByTeacherKeycloakId(@Param("teacherKeycloakId") String teacherKeycloakId);

}
