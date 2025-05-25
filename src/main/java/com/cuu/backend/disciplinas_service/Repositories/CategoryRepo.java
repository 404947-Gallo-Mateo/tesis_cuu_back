package com.cuu.backend.disciplinas_service.Repositories;

import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepo extends JpaRepository<Category, UUID> {

    //sor 'normal':
    // ORDER BY c.ageRange.minAge ASC, c.ageRange.maxAge ASC
    //mismo sort, pero tiene en cuenta nulos:
    // ORDER BY COALESCE(c.ageRange.minAge, 0) ASC, COALESCE(c.ageRange.maxAge, 0) ASC
    @Query("SELECT c FROM Category c " +
            "WHERE c.discipline.id = :disciplineId " +
            "ORDER BY c.ageRange.minAge ASC, c.ageRange.maxAge ASC")
    List<Category> findAllByDisciplineId(@Param("disciplineId") UUID disciplineId);

    // Tu otra consulta permanece igual
    @Query("SELECT c FROM Category c JOIN c.schedules s " +
            "WHERE s.dayOfWeek = :dayOfWeek AND s.startHour = :startHour AND s.endHour = :endHour")
    List<Category> findBySchedule(@Param("dayOfWeek") DayOfWeek dayOfWeek,
                                  @Param("startHour") LocalTime startHour,
                                  @Param("endHour") LocalTime endHour);

}
