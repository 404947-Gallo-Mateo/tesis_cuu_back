package com.cuu.backend.disciplinas_service.Services.Interfaces;

import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentInscriptionService {

    StudentInscriptionDTO createStudentInscription (StudentInscriptionDTO studentInscriptionDTO);

    StudentInscriptionDTO updateStudentInscription (StudentInscriptionDTO studentInscriptionDTO);

    boolean deleteStudentInscriptionByMultipleIDs (String studentKeycloaId, UUID disciplineId, UUID categoryId);

    List<StudentInscriptionDTO> findAllByStudentKeycloakId(String studentKeycloakId);

    List<StudentInscriptionDTO> findAllByDisciplineId(UUID disciplineId);

    List<StudentInscriptionDTO> findAllByCategoryId(UUID categoryId);

    StudentInscriptionDTO findByStudentKeycloakIdAndDisciplineIdAndCategoryId(String studentKeycloakId, UUID disciplineId, UUID categoryId);

    StudentInscriptionDTO findByStudentKeycloakIdAndDisciplineId(String studentKeycloakId, UUID disciplineId);

}
