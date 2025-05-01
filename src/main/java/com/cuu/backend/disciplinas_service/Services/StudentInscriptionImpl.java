package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;
import com.cuu.backend.disciplinas_service.Repositories.StudentInscriptionRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.StudentInscriptionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StudentInscriptionImpl implements StudentInscriptionService {

    @Autowired
    private StudentInscriptionRepo studentInscriptionRepo;

    @Autowired
    private ModelMapper mapper;

    @Override
    public StudentInscriptionDTO createStudentInscription(StudentInscriptionDTO studentInscriptionDTO) {
        //todo validar

        StudentInscription newStudentInscription = mapper.map(studentInscriptionDTO, StudentInscription.class);

        StudentInscription createdStudentInscription = studentInscriptionRepo.save(newStudentInscription);

        return mapper.map(createdStudentInscription, StudentInscriptionDTO.class);
    }

    @Override
    public StudentInscriptionDTO updateStudentInscription(StudentInscriptionDTO studentInscriptionDTO) {
        Optional<StudentInscription> oldStudentInscription = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineIdAndCategoryId(studentInscriptionDTO.getStudent().getKeycloakId(), studentInscriptionDTO.getDiscipline().getId(), studentInscriptionDTO.getCategory().getId());

        //todo validar


        StudentInscription updatedStudentInscription = mapper.map(studentInscriptionDTO, StudentInscription.class);
        updatedStudentInscription.setId(oldStudentInscription.get().getId());

        StudentInscription savedStudentInscription = studentInscriptionRepo.save(updatedStudentInscription);

        return mapper.map(savedStudentInscription, StudentInscriptionDTO.class);
    }

    @Override
    public void deleteStudentInscription(StudentInscriptionDTO studentInscriptionDTO) {
        Optional<StudentInscription> studentInscription = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineIdAndCategoryId(studentInscriptionDTO.getStudent().getKeycloakId(), studentInscriptionDTO.getDiscipline().getId(), studentInscriptionDTO.getCategory().getId());

        studentInscription.ifPresent(value -> studentInscriptionRepo.delete(value));
    }

    @Override
    public List<StudentInscriptionDTO> findAllByStudentKeycloakId(String studentKeycloakId) {
        List<StudentInscription> studentInscriptions = studentInscriptionRepo.findAllByStudentKeycloakId(studentKeycloakId);

        List<StudentInscriptionDTO> studentInscriptionDTOList = new ArrayList<>();

        for (StudentInscription si : studentInscriptions){
            studentInscriptionDTOList.add(mapper.map(si, StudentInscriptionDTO.class));

        }

        return studentInscriptionDTOList;
    }

    @Override
    public List<StudentInscriptionDTO> findAllByDisciplineId(UUID disciplineId) {
        List<StudentInscription> studentInscriptions = studentInscriptionRepo.findAllByDisciplineId(disciplineId);

        List<StudentInscriptionDTO> studentInscriptionDTOList = new ArrayList<>();

        for (StudentInscription si : studentInscriptions){
            studentInscriptionDTOList.add(mapper.map(si, StudentInscriptionDTO.class));

        }

        return studentInscriptionDTOList;
    }

    @Override
    public List<StudentInscriptionDTO> findAllByCategoryId(UUID categoryId) {
        List<StudentInscription> studentInscriptions = studentInscriptionRepo.findAllByCategoryIdAndDisciplineId(categoryId);

        List<StudentInscriptionDTO> studentInscriptionDTOList = new ArrayList<>();

        for (StudentInscription si : studentInscriptions){
            studentInscriptionDTOList.add(mapper.map(si, StudentInscriptionDTO.class));

        }

        return studentInscriptionDTOList;
    }

    @Override
    public Optional<StudentInscription> findByStudentKeycloakIdAndDisciplineIdAndCategoryId(String studentKeycloakId, UUID disciplineId, UUID categoryId){
        return studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineIdAndCategoryId(studentKeycloakId, disciplineId, categoryId);
    }
}
