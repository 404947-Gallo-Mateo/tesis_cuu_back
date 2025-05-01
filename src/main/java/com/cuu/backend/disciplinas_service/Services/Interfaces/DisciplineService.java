package com.cuu.backend.disciplinas_service.Services.Interfaces;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;

import java.util.List;
import java.util.UUID;

public interface DisciplineService {

    DisciplineDTO registerDiscipline(DisciplineDTO disciplineDTO);

    DisciplineDTO updateDiscipline(DisciplineDTO disciplineDTO);

    void deleteDiscipline(DisciplineDTO disciplineDTO);

    DisciplineDTO findByName(String name);

    DisciplineDTO findById(UUID uuid);

    List<DisciplineDTO> findAllByTeacherKeycloakId(String teacherKeycloakId);
}
