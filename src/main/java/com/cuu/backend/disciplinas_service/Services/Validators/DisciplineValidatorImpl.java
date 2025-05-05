package com.cuu.backend.disciplinas_service.Services.Validators;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostDisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.DisciplineRepo;
import com.cuu.backend.disciplinas_service.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DisciplineValidatorImpl {

    @Autowired
    private DisciplineRepo disciplineRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CategoryRepo categoryRepo;

    public void validatePostDisciplineDTO(PostDisciplineDTO disciplineDTO) {
        if (disciplineDTO.getName().length() > 100){
            throw new CustomException("El nombre de la Disciplina no debe superar los 100 caracteres", HttpStatus.BAD_REQUEST);
        }

        if (disciplineDTO.getDescription().length() > 1000){
            throw new CustomException("la descripción de la Disciplina no debe superar los 1000 caracteres", HttpStatus.BAD_REQUEST);
        }

        List<Discipline> allDisciplines = disciplineRepo.findAll();

        for (Discipline d : allDisciplines){
            if (d.getName().equalsIgnoreCase(disciplineDTO.getName())){
                throw new CustomException("Ya existe una Disciplina con nombre " + disciplineDTO.getName(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    public Discipline validatePutDisciplineDTO(DisciplineDTO disciplineDTO) {
        Optional<Discipline> oldDisciplineOpt = disciplineRepo.findById(disciplineDTO.getId());

        if (oldDisciplineOpt.isEmpty()){
            throw new CustomException("No se pudo actualizar la Disciplina porque no existe un registro previo", HttpStatus.BAD_REQUEST);
        }

        if (disciplineDTO.getName().length() > 100){
            throw new CustomException("El nombre de la Disciplina no debe superar los 100 caracteres", HttpStatus.BAD_REQUEST);
        }

        if (disciplineDTO.getDescription().length() > 1000){
            throw new CustomException("la descripción de la Disciplina no debe superar los 1000 caracteres", HttpStatus.BAD_REQUEST);
        }

        List<Discipline> allDisciplines = disciplineRepo.findAll();

        for (Discipline d : allDisciplines){
            if (d.getName().equalsIgnoreCase(disciplineDTO.getName()) && d.getId() != disciplineDTO.getId()){
                throw new CustomException("Ya existe una Disciplina con nombre " + disciplineDTO.getName(), HttpStatus.BAD_REQUEST);
            }
        }

        return oldDisciplineOpt.get();
    }

}
