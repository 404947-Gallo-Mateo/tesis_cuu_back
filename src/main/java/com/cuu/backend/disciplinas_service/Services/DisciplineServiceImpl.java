package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Repositories.DisciplineRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.DisciplineService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DisciplineServiceImpl implements DisciplineService {

    @Autowired
    private DisciplineRepo disciplineRepo;

    @Autowired
    private ModelMapper mapper;


    @Override
    public DisciplineDTO registerDiscipline(DisciplineDTO disciplineDTO) {
        //todo validar

        Discipline newDiscipline = mapper.map(disciplineDTO, Discipline.class);

        Discipline createdDiscipline = disciplineRepo.save(newDiscipline);

        return mapper.map(createdDiscipline, DisciplineDTO.class);
    }

    @Override
    public DisciplineDTO updateDiscipline(DisciplineDTO disciplineDTO) {
        Optional<Discipline> oldDisciplineOpt = disciplineRepo.findById(disciplineDTO.getId());

        //todo validar

        Discipline oldDiscipline = oldDisciplineOpt.get();

        Discipline updatedDiscipline = mapper.map(disciplineDTO, Discipline.class);
        updatedDiscipline.setId(oldDiscipline.getId());

        Discipline savedDiscipline = disciplineRepo.save(updatedDiscipline);

        return mapper.map(savedDiscipline, DisciplineDTO.class);


    }

    @Override
    public boolean deleteDiscipline(DisciplineDTO disciplineDTO) {
        Optional<Discipline> discipline = disciplineRepo.findByName(disciplineDTO.getName());

        if (discipline.isPresent()) {
            disciplineRepo.delete(discipline.get());
            return true;
        }

        return false;        }

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
