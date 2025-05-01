package com.cuu.backend.disciplinas_service.Repositories;

import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepo extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM Category c WHERE c.discipline.id = :disciplineId")
    List<Category> findAllByDisciplineId(@Param("disciplineId") UUID disciplineId);

}
