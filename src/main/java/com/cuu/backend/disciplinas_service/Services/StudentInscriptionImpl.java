package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.CategorySummaryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.DisciplineSummaryDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.DisciplineRepo;
import com.cuu.backend.disciplinas_service.Repositories.StudentInscriptionRepo;
import com.cuu.backend.disciplinas_service.Repositories.UserRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.StudentInscriptionService;
import com.cuu.backend.disciplinas_service.Services.Validators.StudentInscriptionValidatorImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentInscriptionImpl implements StudentInscriptionService {

    @Autowired
    private StudentInscriptionRepo studentInscriptionRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private DisciplineRepo disciplineRepo;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private StudentInscriptionValidatorImpl validator;

    @Override
    public StudentInscriptionDTO createStudentInscription(StudentInscriptionDTO studentInscriptionDTO) {
        StudentInscription newStudentInscription = validator.validatePostStudentInscriptionDTO(studentInscriptionDTO);

        StudentInscription savedStudentInscription = studentInscriptionRepo.save(newStudentInscription);

        StudentInscriptionDTO savedStudentInscriptionDTO = mapper.map(savedStudentInscription, StudentInscriptionDTO.class);

        savedStudentInscriptionDTO.setDiscipline(new DisciplineSummaryDTO(savedStudentInscriptionDTO.getDiscipline().getId(), savedStudentInscriptionDTO.getDiscipline().getName()));
        savedStudentInscriptionDTO.setCategory(new CategorySummaryDTO(savedStudentInscriptionDTO.getCategory().getId(), savedStudentInscriptionDTO.getCategory().getName(), savedStudentInscriptionDTO.getDiscipline().getId(), savedStudentInscriptionDTO.getDiscipline().getName()));

        return savedStudentInscriptionDTO;
    }

    //se usa SOLO para cambiar de Category a un Alumno, ya q sigue dentro de la misma Discipline.
    // (y como no puede estar en 2 Categories de una misma Discipline al mismo tiempo, se hace UPDATE en la original,
    //  en vez de DELETE la original y CREATE otra nueva actualizada)
    @Override
    public StudentInscriptionDTO updateStudentInscription(StudentInscriptionDTO studentInscriptionDTO) {
        StudentInscription updatedStudentInscription = validator.validatePutStudentInscriptionDTO(studentInscriptionDTO);

        StudentInscription savedStudentInscription = studentInscriptionRepo.save(updatedStudentInscription);

        StudentInscriptionDTO savedStudentInscriptionDTO = mapper.map(savedStudentInscription, StudentInscriptionDTO.class);

        savedStudentInscriptionDTO.setDiscipline(new DisciplineSummaryDTO(savedStudentInscriptionDTO.getDiscipline().getId(), savedStudentInscriptionDTO.getDiscipline().getName()));
        savedStudentInscriptionDTO.setCategory(new CategorySummaryDTO(savedStudentInscriptionDTO.getCategory().getId(), savedStudentInscriptionDTO.getCategory().getName(), savedStudentInscriptionDTO.getDiscipline().getId(), savedStudentInscriptionDTO.getDiscipline().getName()));

        return savedStudentInscriptionDTO;
    }

    @Override
    public boolean deleteStudentInscriptionByMultipleIDs(String studentKeycloakId, UUID disciplineId, UUID categoryId) {
        Optional<StudentInscription> studentInscription = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineIdAndCategoryId(studentKeycloakId, disciplineId, categoryId);

        if (studentInscription.isPresent()) {
            studentInscriptionRepo.delete(studentInscription.get());
            return true;
        }

        return false;
    }

    @Override
    public List<StudentInscriptionDTO> findAllByStudentKeycloakId(String studentKeycloakId) {
        List<StudentInscription> studentInscriptions = studentInscriptionRepo.findAllByStudentKeycloakId(studentKeycloakId);

        List<StudentInscriptionDTO> studentInscriptionDTOList = new ArrayList<>();

        for (StudentInscription si : studentInscriptions){
            StudentInscriptionDTO studentInscriptionDTO = mapper.map(si, StudentInscriptionDTO.class);

            studentInscriptionDTO.setDiscipline(new DisciplineSummaryDTO(si.getDiscipline().getId(), si.getDiscipline().getName()));
            studentInscriptionDTO.setCategory(new CategorySummaryDTO(si.getCategory().getId(), si.getCategory().getName(), si.getDiscipline().getId(), si.getDiscipline().getName()));

            studentInscriptionDTOList.add(studentInscriptionDTO);

        }

        return studentInscriptionDTOList;
    }

    @Override
    public List<StudentInscriptionDTO> findAllByDisciplineId(UUID disciplineId) {
        List<StudentInscription> studentInscriptions = studentInscriptionRepo.findAllByDisciplineId(disciplineId);

        List<StudentInscriptionDTO> studentInscriptionDTOList = new ArrayList<>();

        for (StudentInscription si : studentInscriptions){
            StudentInscriptionDTO studentInscriptionDTO = mapper.map(si, StudentInscriptionDTO.class);

            studentInscriptionDTO.setDiscipline(new DisciplineSummaryDTO(si.getDiscipline().getId(), si.getDiscipline().getName()));
            studentInscriptionDTO.setCategory(new CategorySummaryDTO(si.getCategory().getId(), si.getCategory().getName(), si.getDiscipline().getId(), si.getDiscipline().getName()));

            studentInscriptionDTOList.add(studentInscriptionDTO);

        }

        return studentInscriptionDTOList;
    }

    @Override
    public List<StudentInscriptionDTO> findAllByCategoryId(UUID categoryId) {
        List<StudentInscription> studentInscriptions = studentInscriptionRepo.findAllByCategoryId(categoryId);

        List<StudentInscriptionDTO> studentInscriptionDTOList = new ArrayList<>();

        for (StudentInscription si : studentInscriptions){
            StudentInscriptionDTO studentInscriptionDTO = mapper.map(si, StudentInscriptionDTO.class);

            studentInscriptionDTO.setDiscipline(new DisciplineSummaryDTO(si.getDiscipline().getId(), si.getDiscipline().getName()));
            studentInscriptionDTO.setCategory(new CategorySummaryDTO(si.getCategory().getId(), si.getCategory().getName(), si.getDiscipline().getId(), si.getDiscipline().getName()));

            studentInscriptionDTOList.add(studentInscriptionDTO);

        }

        return studentInscriptionDTOList;
    }

    @Override
    public StudentInscriptionDTO findByStudentKeycloakIdAndDisciplineIdAndCategoryId(String studentKeycloakId, UUID disciplineId, UUID categoryId){
        Optional<StudentInscription> studentInscriptionOpt = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineIdAndCategoryId(studentKeycloakId, disciplineId, categoryId);

        if (studentInscriptionOpt.isEmpty()){
            throw new CustomException("No se pudo encontrar StudentInscription con los 3 atributos indicados", HttpStatus.BAD_REQUEST);

        }

        StudentInscription si = studentInscriptionOpt.get();

        StudentInscriptionDTO studentInscriptionDTO = mapper.map(si, StudentInscriptionDTO.class);

        studentInscriptionDTO.setDiscipline(new DisciplineSummaryDTO(si.getDiscipline().getId(), si.getDiscipline().getName()));
        studentInscriptionDTO.setCategory(new CategorySummaryDTO(si.getCategory().getId(), si.getCategory().getName(), si.getDiscipline().getId(), si.getDiscipline().getName()));

        return studentInscriptionDTO;
    }

    @Override
    public StudentInscriptionDTO findByStudentKeycloakIdAndDisciplineId(String studentKeycloakId, UUID disciplineId) {
        Optional<StudentInscription> studentInscriptionOpt = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineId(studentKeycloakId, disciplineId);

        if (studentInscriptionOpt.isEmpty()){
            throw new CustomException("No se pudo encontrar StudentInscription con los 2 atributos indicados", HttpStatus.BAD_REQUEST);
        }

        StudentInscription si = studentInscriptionOpt.get();

        StudentInscriptionDTO studentInscriptionDTO = mapper.map(si, StudentInscriptionDTO.class);

        studentInscriptionDTO.setDiscipline(new DisciplineSummaryDTO(si.getDiscipline().getId(), si.getDiscipline().getName()));
        studentInscriptionDTO.setCategory(new CategorySummaryDTO(si.getCategory().getId(), si.getCategory().getName(), si.getDiscipline().getId(), si.getDiscipline().getName()));

        return studentInscriptionDTO;
    }


}
