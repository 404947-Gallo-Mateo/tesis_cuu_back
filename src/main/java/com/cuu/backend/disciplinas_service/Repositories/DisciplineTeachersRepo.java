package com.cuu.backend.disciplinas_service.Repositories;

import com.cuu.backend.disciplinas_service.Models.Entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DisciplineTeachersRepo extends JpaRepository<User, UUID> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM teacher_disciplines WHERE discipline_id = :disciplineId", nativeQuery = true)
    void deleteTeacherDisciplineRelations(@Param("disciplineId") UUID disciplineId);
}
