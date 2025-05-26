package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostDisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PutDisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.DisciplineRepo;
import com.cuu.backend.disciplinas_service.Repositories.UserRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.DisciplineService;
import com.cuu.backend.disciplinas_service.Services.Mappers.ComplexMapper;
import com.cuu.backend.disciplinas_service.Services.Validators.CategoryValidatorImpl;
import com.cuu.backend.disciplinas_service.Services.Validators.DisciplineValidatorImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DisciplineServiceImpl implements DisciplineService {

    @Autowired
    private DisciplineRepo disciplineRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private DisciplineValidatorImpl disciplineValidator;
    @Autowired
    private CategoryValidatorImpl categoryValidator;
    @Autowired
    private ComplexMapper complexMapper;

    @Autowired
    private ModelMapper mapper;


    @Override
    public DisciplineDTO createDiscipline(PostDisciplineDTO disciplineDTO) {
        disciplineValidator.validatePostDisciplineDTO(disciplineDTO);
        categoryValidator.validatePostCategoryDTO(disciplineDTO.getCategories());

        Discipline newDiscipline = complexMapper.mapPostDTOToDiscipline(disciplineDTO);

        Discipline createdDiscipline = disciplineRepo.save(newDiscipline);

        return complexMapper.mapDisciplineEntityToDisciplineDTO(createdDiscipline);
    }


    @Override
    public DisciplineDTO updateDiscipline(PutDisciplineDTO disciplineDTO) {
        Discipline oldDiscipline = disciplineValidator.validatePutDisciplineDTO(disciplineDTO);

        categoryValidator.validatePutCategoryDTO(disciplineDTO.getCategories());

        Discipline updatedDiscipline = complexMapper.mapDisciplineDTOToDiscipline(disciplineDTO, oldDiscipline);

        Discipline savedDiscipline = disciplineRepo.save(updatedDiscipline);

        return complexMapper.mapDisciplineEntityToDisciplineDTO(savedDiscipline);
    }

    @Override
    public boolean deleteDisciplineById(UUID disciplineId) {
        Optional<Discipline> discipline = disciplineRepo.findById(disciplineId);

        if (discipline.isPresent()) {
            disciplineRepo.delete(discipline.get());
            return true;
        }

        return false;
    }

    @Override
    public List<DisciplineDTO> getAll(){
        List<Discipline> disciplines = disciplineRepo.findAllWithCategoriesOrdered();

        List<DisciplineDTO> disciplineDTOList = new ArrayList<>();

        for (Discipline d : disciplines){
            disciplineDTOList.add(complexMapper.mapDisciplineEntityToDisciplineDTO(d));
        }

        return disciplineDTOList;
    }


    @Override
    public DisciplineDTO findByName(String name) {
        Optional<Discipline> discipline = disciplineRepo.findByName(name);

        if (discipline.isPresent()){
            return complexMapper.mapDisciplineEntityToDisciplineDTO(discipline.get());
        }
        else {
            return null;
        }

    }

    @Override
    public DisciplineDTO findById(UUID id) {
        Optional<Discipline> discipline = disciplineRepo.findById(id);

        if (discipline.isPresent()){
            return complexMapper.mapDisciplineEntityToDisciplineDTO(discipline.get());
        }
        else {
            return null;
        }

    }

    @Override
    public List<DisciplineDTO> findAllByTeacherKeycloakId(String teacherKeycloakId) {
        List<Discipline> disciplines = disciplineRepo.findAllByTeacherKeycloakId(teacherKeycloakId);

        List<DisciplineDTO> disciplineDTOList = new ArrayList<>();

        for (Discipline d : disciplines){
            disciplineDTOList.add(complexMapper.mapDisciplineEntityToDisciplineDTO(d));

        }

        return disciplineDTOList;
    }

}
