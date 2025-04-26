package com.cuu.backend.disciplinas_service.Repositories;


import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentInscriptionRepo extends JpaRepository<StudentInscription, UUID> {
    //traen el objeto entero
    @Query("SELECT si FROM StudentInscription si WHERE si.student.id = :studentId")
    List<StudentInscription> findAllByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT si FROM StudentInscription si WHERE si.discipline.id = :disciplineId")
    List<StudentInscription> findAllByDisciplineId(@Param("disciplineId") UUID disciplineId);

    @Query("SELECT si FROM StudentInscription si WHERE si.category.id = :categoryId")
    List<StudentInscription> findAllByCategoryId(@Param("categoryId") UUID categoryId);

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
