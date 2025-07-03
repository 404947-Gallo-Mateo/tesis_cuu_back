package com.cuu.backend.disciplinas_service.Repositories;


import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentInscriptionRepo extends JpaRepository<StudentInscription, UUID> {
    //traen el objeto entero
    @Query("SELECT si FROM StudentInscription si " +
            "WHERE si.student.keycloakId = :studentKeycloakId " +
            "ORDER BY si.discipline.name ASC")
    List<StudentInscription> findAllByStudentKeycloakId(@Param("studentKeycloakId") String studentKeycloakId);

    @Query("SELECT si FROM StudentInscription si " +
            "WHERE si.discipline.id = :disciplineId " +
            "ORDER BY si.discipline.name ASC, si.category.ageRange.minAge ASC, si.category.ageRange.maxAge ASC")
    List<StudentInscription> findAllByDisciplineId(@Param("disciplineId") UUID disciplineId);

    @Query("SELECT si FROM StudentInscription si " +
            "WHERE si.category.id = :categoryId " +
            "ORDER BY si.discipline.name ASC, si.student.lastName ASC, si.student.firstName ASC")
    List<StudentInscription> findAllByCategoryId(@Param("categoryId") UUID categoryId);

    List<StudentInscription> findByCategory(Category category);

    @Query("SELECT COUNT(si) FROM StudentInscription si " +
            "WHERE si.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") UUID categoryId);

    @Query("SELECT si FROM StudentInscription si " +
            "WHERE si.student.keycloakId =:studentKeycloakId AND si.discipline.id = :disciplineId AND si.category.id = :categoryId")
    Optional<StudentInscription> findByStudentKeycloakIdAndDisciplineIdAndCategoryId(@Param("studentKeycloakId") String studentKeycloakId, @Param("disciplineId") UUID disciplineId, @Param("categoryId") UUID categoryId);

    @Query("SELECT si FROM StudentInscription si " +
            "WHERE si.discipline.id = :disciplineId AND si.student.keycloakId =:studentKeycloakId")
    Optional<StudentInscription> findByStudentKeycloakIdAndDisciplineId(@Param("studentKeycloakId") String studentKeycloakId, @Param("disciplineId") UUID disciplineId);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentInscription si WHERE si.discipline.id = :disciplineId")
    void deleteByDisciplineId(@Param("disciplineId") UUID disciplineId);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentInscription si WHERE si.category.id IN (SELECT c.id FROM Category c WHERE c.discipline.id = :disciplineId)")
    void deleteByDisciplineCategories(@Param("disciplineId") UUID disciplineId);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentInscription si WHERE si.student.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    @Query("SELECT si FROM StudentInscription si " +
            "WHERE si.student.keycloakId = :studentKeycloakId " +
            "ORDER BY si.createdDate ASC LIMIT 1")
    Optional<StudentInscription> findOldestByStudentKeycloakId(@Param("studentKeycloakId") String studentKeycloakId);

    //traen solo el ID de StudentInscription
//    @Query("SELECT si.id FROM StudentInscription si WHERE si.student.id = :studentId")
//    List<UUID> findIdsByStudentId(@Param("studentId") UUID studentId);
//
//    @Query("SELECT si.id FROM StudentInscription si WHERE si.discipline.id = :disciplineId")
//    List<UUID> findIdsByDisciplineId(@Param("disciplineId") UUID disciplineId);
//
//    @Query("SELECT si.id FROM StudentInscription si WHERE si.category.id = :categoryId")
//    List<UUID> findIdsByCategoryId(@Param("categoryId") UUID categoryId);
}
