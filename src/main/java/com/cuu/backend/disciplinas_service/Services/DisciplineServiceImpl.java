package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.UserDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostCategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostDisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.DisciplineRepo;
import com.cuu.backend.disciplinas_service.Repositories.UserRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.DisciplineService;
import com.cuu.backend.disciplinas_service.Services.Mappers.ComplexMapper;
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
    private DisciplineValidatorImpl validator;
    @Autowired
    private ComplexMapper complexMapper;

    @Autowired
    private ModelMapper mapper;


    @Override
    public DisciplineDTO createDiscipline(PostDisciplineDTO disciplineDTO) {
        validator.validatePostDisciplineDTO(disciplineDTO);

        Discipline newDiscipline = complexMapper.mapPostDTOToDiscipline(disciplineDTO);

        Discipline createdDiscipline = disciplineRepo.save(newDiscipline);

        return mapper.map(createdDiscipline, DisciplineDTO.class);
    }


    @Override
    public DisciplineDTO updateDiscipline(DisciplineDTO disciplineDTO) {
        Discipline oldDiscipline = validator.validatePutDisciplineDTO(disciplineDTO);

        Discipline updatedDiscipline =complexMapper.mapDisciplineDTOToDiscipline(disciplineDTO, oldDiscipline);

        Discipline savedDiscipline = disciplineRepo.save(updatedDiscipline);

        return mapper.map(savedDiscipline, DisciplineDTO.class);
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
        List<Discipline> disciplines = disciplineRepo.findAll();

        List<DisciplineDTO> disciplineDTOList = new ArrayList<>();

        for (Discipline d : disciplines){
            disciplineDTOList.add(mapper.map(d, DisciplineDTO.class));

        }

        return disciplineDTOList;
    }


    @Override
    public DisciplineDTO findByName(String name) {
        Optional<Discipline> discipline = disciplineRepo.findByName(name);

        if (discipline.isPresent()){
            return mapper.map(discipline.get(), DisciplineDTO.class);
        }
        else {
            return null;
        }

    }

    @Override
    public DisciplineDTO findById(UUID id) {
        Optional<Discipline> discipline = disciplineRepo.findById(id);

        if (discipline.isPresent()){
            return mapper.map(discipline.get(), DisciplineDTO.class);
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
            disciplineDTOList.add(mapper.map(d, DisciplineDTO.class));

        }

        return disciplineDTOList;
    }

}
