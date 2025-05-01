package com.cuu.backend.disciplinas_service.Services.Interfaces;

import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentInscriptionService {

    StudentInscriptionDTO createStudentInscription (StudentInscriptionDTO studentInscriptionDTO);

    StudentInscriptionDTO updateStudentInscription (StudentInscriptionDTO studentInscriptionDTO);

    boolean deleteStudentInscription (StudentInscriptionDTO studentInscriptionDTO);

    List<StudentInscriptionDTO> findAllByStudentKeycloakId(String studentKeycloakId);

    List<StudentInscriptionDTO> findAllByDisciplineId(UUID disciplineId);

    List<StudentInscriptionDTO> findAllByCategoryId(UUID categoryId);

    Optional<StudentInscription> findByStudentKeycloakIdAndDisciplineIdAndCategoryId(String studentKeycloakId, UUID disciplineId, UUID categoryId);

    Optional<StudentInscription> findByStudentKeycloakIdAndDisciplineId(String studentKeycloakId, UUID disciplineId);

}
