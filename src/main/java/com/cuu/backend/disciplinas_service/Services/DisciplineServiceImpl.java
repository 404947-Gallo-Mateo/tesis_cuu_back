package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostDisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PutCategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PutDisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Repositories.*;
import com.cuu.backend.disciplinas_service.Services.Interfaces.DisciplineService;
import com.cuu.backend.disciplinas_service.Services.Mappers.ComplexMapper;
import com.cuu.backend.disciplinas_service.Services.Validators.CategoryValidatorImpl;
import com.cuu.backend.disciplinas_service.Services.Validators.DisciplineValidatorImpl;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class DisciplineServiceImpl implements DisciplineService {

    @Autowired
    private DisciplineRepo disciplineRepo;
    @Autowired
    private DisciplineTeachersRepo disciplineTeachersRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private StudentInscriptionRepo studentInscriptionRepo;

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

    //
    @Override
    @Transactional
    public DisciplineDTO updateDiscipline(PutDisciplineDTO disciplineDTO) {
        Discipline oldDiscipline = disciplineValidator.validatePutDisciplineDTO(disciplineDTO);
        categoryValidator.validatePutCategoryDTO(disciplineDTO.getCategories());

        // Obtener las categorías antes de la actualización
        List<Category> oldCategories = new ArrayList<>(oldDiscipline.getCategories());

        // Obtener categorías después de la actualización
        List<PutCategoryDTO> updatedCategories = disciplineDTO.getCategories();

        // Identificar categorías eliminadas (presentes en oldCategories pero no en updatedCategories)
        List<Category> categoriesToRemove = oldCategories.stream()
                .filter(oldCat -> updatedCategories.stream()
                        .noneMatch(updatedCat -> updatedCat.getId() != null && updatedCat.getId().equals(oldCat.getId())))
                .toList();

        // PRIMERO: Eliminar StudentInscriptions que referencian las categorías a eliminar
        if (!categoriesToRemove.isEmpty()) {
            for (Category categoryToRemove : categoriesToRemove) {
                // Buscar todas las StudentInscriptions que contengan esta categoría
                List<StudentInscription> inscriptionsToDelete = studentInscriptionRepo
                        .findByCategory(categoryToRemove);

                // Eliminar las inscripciones encontradas
                if (!inscriptionsToDelete.isEmpty()) {
                    studentInscriptionRepo.deleteAll(inscriptionsToDelete);
                }
            }
            // Flush para asegurar que las eliminaciones se ejecuten antes del mapeo
            studentInscriptionRepo.flush();
        }

        // Eliminar relación entre TEACHERS y la Discipline (en caso de que hayan cambiado los TEACHERS asignados)
        List<User> oldTeachers = new ArrayList<>(oldDiscipline.getTeachers());
        List<String> updatedTeachers = disciplineDTO.getTeacherIds();

        // Identificar profesores a remover
                List<User> teachersToRemove = oldTeachers.stream()
                        .filter(oldTeach -> updatedTeachers.stream()
                                .noneMatch(updatedTeach ->
                                        updatedTeach != null &&
                                                updatedTeach.equals(oldTeach.getId().toString())
                                )
                        )
                        .toList();

        // Remover relación desde el lado del profesor
                if (!teachersToRemove.isEmpty()) {
                    for (User teacher : teachersToRemove) {
                        List<Discipline> teacherDisciplines = new ArrayList<>(teacher.getTeacherDisciplines()); // Crear copia mutable

                        // Buscar índice de la disciplina a eliminar
                        int indexToRemove = -1;
                        for (int i = 0; i < teacherDisciplines.size(); i++) {
                            Discipline disc = teacherDisciplines.get(i);
                            if (disc.getId() != null && disc.getId().equals(disciplineDTO.getId())) {
                                indexToRemove = i;
                                break;
                            }
                        }

                        // Eliminar por índice si se encontró
                        if (indexToRemove != -1) {
                            teacherDisciplines.remove(indexToRemove);
                            teacher.setTeacherDisciplines(teacherDisciplines); // Actualizar lista
                            userRepo.save(teacher); // Guardar cambios
                        }
                    }
                }
        // FIN relación entre TEACHERS y la Discipline


        // SEGUNDO: Ahora mapear y actualizar la disciplina (esto eliminará las categorías)
        Discipline updatedDiscipline = complexMapper.mapDisciplineDTOToDiscipline(disciplineDTO, oldDiscipline);

        // Guardar la disciplina actualizada
        Discipline savedDiscipline = disciplineRepo.save(updatedDiscipline);
        return complexMapper.mapDisciplineEntityToDisciplineDTO(savedDiscipline);
    }

    @Override
    @Transactional
    public boolean deleteDisciplineById(UUID disciplineId) {
        Optional<Discipline> discipline = disciplineRepo.findById(disciplineId);

        if (discipline.isPresent()) {

            // 1. Eliminar inscripciones de estudiantes relacionadas con la disciplina
            studentInscriptionRepo.deleteByDisciplineId(disciplineId);

            // 2. Eliminar inscripciones de estudiantes relacionadas con categorías de la disciplina
            studentInscriptionRepo.deleteByDisciplineCategories(disciplineId);

            // 3. Eliminar relaciones teacher-discipline (ambas tablas de unión)
            disciplineTeachersRepo.deleteTeacherDisciplineRelations(disciplineId);
            disciplineRepo.deleteDisciplineTeacherRelations(disciplineId);

            // 4. Eliminar categorías (se hará en cascada si está configurado)
            // 5. Finalmente eliminar la disciplina
            disciplineRepo.deleteById(disciplineId);
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
