package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private ModelMapper mapper;


    @Override
    public DisciplineDTO createDiscipline(PostDisciplineDTO disciplineDTO) {
        //todo validar
        Discipline newDiscipline = mapPostDTOToDiscipline(disciplineDTO);

        Discipline createdDiscipline = disciplineRepo.save(newDiscipline);

        return mapper.map(createdDiscipline, DisciplineDTO.class);
    }

    @Override
    public DisciplineDTO updateDiscipline(DisciplineDTO disciplineDTO) {
        Optional<Discipline> oldDisciplineOpt = disciplineRepo.findById(disciplineDTO.getId());

        //todo validar
        if (oldDisciplineOpt.isEmpty()){
            throw new CustomException("No se pudo actualizar la Disciplina porque no existe un registro previo", HttpStatus.BAD_REQUEST);
        }

        Discipline updatedDiscipline = mapDisciplineDTOToDiscipline(disciplineDTO, oldDisciplineOpt.get());

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

    //mapeos manuales
    private Discipline mapPostDTOToDiscipline(PostDisciplineDTO disciplineDTO) {
        List<User> teachers = new ArrayList<>();

        for (UserDTO teacherDTO : disciplineDTO.getTeachers()){
            Optional<User> teacher = userRepo.findByKeycloakId(teacherDTO.getKeycloakId());

            if (teacher.isPresent()){
                teachers.add(teacher.get());
            }
            else{
                User newTeacher = new User(null, teacherDTO.getKeycloakId(), teacherDTO.getRole(), teacherDTO.getUsername(), teacherDTO.getEmail(), teacherDTO.getFirstName(), teacherDTO.getLastName(), teacherDTO.getBirthDate(), teacherDTO.getGenre(), null);
                userRepo.save(newTeacher);
                teachers.add(newTeacher);
            }
        }

        List<Category> categories = new ArrayList<>();

        for (PostCategoryDTO postDTO : disciplineDTO.getCategories()){
            Category newCategory = new Category(null, postDTO.getName(), postDTO.getDescription(), postDTO.getMonthlyFee(), null, postDTO.getAvailableSpaces(), postDTO.getAgeRange(), postDTO.getSchedule(), postDTO.getAllowedGenre());

            categories.add(newCategory);
        }

        Discipline newDiscipline = new Discipline(null, disciplineDTO.getName(), disciplineDTO.getDescription(), teachers, categories);

        for (Category c : newDiscipline.getCategories()){
            c.setDiscipline(newDiscipline);
        }

        return newDiscipline;
    }

    private Discipline mapDisciplineDTOToDiscipline(DisciplineDTO updatedDisciplineDTO, Discipline oldDiscipline) {
        List<User> teachers = getTeachersFromDiscipline(updatedDisciplineDTO, oldDiscipline);

        List<Category> newCategories = getCategoriesFromDiscipline(updatedDisciplineDTO, oldDiscipline);

        // Quitar categorías que ya no están en el DTO
        oldDiscipline.getCategories().removeIf(oldCat ->
                newCategories.stream().noneMatch(newCat -> newCat.getId() != null && newCat.getId().equals(oldCat.getId()))
        );

        // Agregar o actualizar categorías
        for (Category newCategory : newCategories) {
            newCategory.setDiscipline(oldDiscipline); // asegurás la relación inversa

            if (!oldDiscipline.getCategories().contains(newCategory)) {
                oldDiscipline.getCategories().add(newCategory);
            }
        }

        oldDiscipline.setName(updatedDisciplineDTO.getName());
        oldDiscipline.setDescription(updatedDisciplineDTO.getDescription());
        oldDiscipline.setTeachers(teachers);

        return oldDiscipline;
    }

//    private Discipline mapDisciplineDTOToDiscipline(DisciplineDTO updatedDisciplineDTO, Discipline oldDiscipline) {
//        List<User> teachers = getTeachersFromDiscipline(updatedDisciplineDTO, oldDiscipline);
//
//        List<Category> categories = getCategoriesFromDiscipline(updatedDisciplineDTO, oldDiscipline);
//
//        oldDiscipline.setName(updatedDisciplineDTO.getName());
//        oldDiscipline.setDescription(updatedDisciplineDTO.getDescription());
//        oldDiscipline.setTeachers(teachers);
//        oldDiscipline.setCategories(categories);
//
//        return oldDiscipline;
//    }

    private List<Category> getCategoriesFromDiscipline(DisciplineDTO updatedDisciplineDTO, Discipline oldDiscipline) {
        List<Category> categories = new ArrayList<>();

        for (CategoryDTO categoryDTO : updatedDisciplineDTO.getCategories()) {
            Optional<Category> optionalCategory = categoryRepo.findById(categoryDTO.getId());

            Category category;
            if (optionalCategory.isPresent()) {
                category = optionalCategory.get();
                // Actualizá sus valores
                category.setName(categoryDTO.getName());
                category.setDescription(categoryDTO.getDescription());
                category.setMonthlyFee(categoryDTO.getMonthlyFee());
                category.setAvailableSpaces(categoryDTO.getAvailableSpaces());
                category.setAgeRange(categoryDTO.getAgeRange());
                category.setSchedule(categoryDTO.getSchedule());
                category.setAllowedGenre(categoryDTO.getAllowedGenre());
            } else {
                category = new Category(
                        null,
                        categoryDTO.getName(),
                        categoryDTO.getDescription(),
                        categoryDTO.getMonthlyFee(),
                        oldDiscipline,
                        categoryDTO.getAvailableSpaces(),
                        categoryDTO.getAgeRange(),
                        categoryDTO.getSchedule(),
                        categoryDTO.getAllowedGenre()
                );
            }

            categories.add(category);
        }

        return categories;
    }

//    private List<Category> getCategoriesFromDiscipline(DisciplineDTO updatedDisciplineDTO, Discipline oldDiscipline) {
//        List<Category> categories = new ArrayList<>();
//
//        for (CategoryDTO categoryDTO : updatedDisciplineDTO.getCategories()) {
//            Optional<Category> category = categoryRepo.findById(categoryDTO.getId());
//
//            if (category.isPresent()) {
//                categories.add(category.get());
//            } else {
//                Category newCategory = new Category(
//                        null,
//                        categoryDTO.getName(),
//                        categoryDTO.getDescription(),
//                        categoryDTO.getMonthlyFee(),
//                        oldDiscipline,
//                        categoryDTO.getAvailableSpaces(),
//                        categoryDTO.getAgeRange(),
//                        categoryDTO.getSchedule(),
//                        categoryDTO.getAllowedGenre()
//                );
//                categories.add(newCategory);
//            }
//        }
//        return categories;
//    }

    private List<User> getTeachersFromDiscipline(DisciplineDTO updatedDisciplineDTO, Discipline oldDiscipline) {
        List<User> teachers = new ArrayList<>();

        for (UserDTO teacherDTO : updatedDisciplineDTO.getTeachers()) {
            Optional<User> teacherOpt = userRepo.findByKeycloakId(teacherDTO.getKeycloakId());

            if (teacherOpt.isPresent()) {
                User existingTeacher = teacherOpt.get();

                if (existingTeacher.getTeacherDisciplines() == null) {
                    existingTeacher.setTeacherDisciplines(new ArrayList<>());
                }

                boolean alreadyAssigned = existingTeacher.getTeacherDisciplines().stream()
                        .anyMatch(d -> d.getId() != null && d.getId().equals(oldDiscipline.getId()));

                if (!alreadyAssigned) {
                    existingTeacher.getTeacherDisciplines().add(oldDiscipline);
                }

                teachers.add(existingTeacher);
            }
            else {
                // Nuevo profesor
                List<Discipline> teacherDisciplines = new ArrayList<>();
                teacherDisciplines.add(oldDiscipline);

                User newTeacher = User.builder()
                        .keycloakId(teacherDTO.getKeycloakId())
                        .role(teacherDTO.getRole())
                        .username(teacherDTO.getUsername())
                        .email(teacherDTO.getEmail())
                        .firstName(teacherDTO.getFirstName())
                        .lastName(teacherDTO.getLastName())
                        .birthDate(teacherDTO.getBirthDate())
                        .genre(teacherDTO.getGenre())
                        .teacherDisciplines(teacherDisciplines)
                        .build();

//                Role role = new Role(EnumRole.TEACHER);
//                newTeacher.setRole(role);

                userRepo.save(newTeacher);
                teachers.add(newTeacher);
            }
        }
        return teachers;
    }


//    private Discipline mapDisciplineDTOToDiscipline(DisciplineDTO updatedDisciplineDTO, Discipline oldDiscipline) {
//        List<User> teachers = new ArrayList<>();
//
//        for (UserDTO teacherDTO : updatedDisciplineDTO.getTeachers()){
//            Optional<User> teacher = userRepo.findByKeycloakId(teacherDTO.getKeycloakId());
//
//            if (teacher.isPresent()){
//                teachers.add(teacher.get());
//            }
//            else{
//                User newTeacher = new User(null, teacherDTO.getKeycloakId(), teacherDTO.getRoles(), teacherDTO.getUsername(), teacherDTO.getEmail(), teacherDTO.getFirstName(), teacherDTO.getLastName(), null, teacherDTO.getBirthDate(), teacherDTO.getGenre());
//                userRepo.save(newTeacher);
//                teachers.add(newTeacher);
//            }
//        }
//
//        List<Category> categories = new ArrayList<>();
//
//        for (CategoryDTO categoryDTO : updatedDisciplineDTO.getCategories()){
//            Optional<Category> category = categoryRepo.findById(categoryDTO.getId());
//
//            if (category.isPresent()){
//                categories.add(category.get());
//            }
//            else{
//                Category newCategory = new Category(null, categoryDTO.getName(), categoryDTO.getDescription(), categoryDTO.getMonthlyFee(), oldDiscipline, categoryDTO.getAvailableSpaces(), categoryDTO.getAgeRange(), categoryDTO.getSchedule(), categoryDTO.getAllowedGenre());
//
//                categories.add(newCategory);
//            }
//        }
//
//        oldDiscipline.setName(updatedDisciplineDTO.getName());
//        oldDiscipline.setDescription(updatedDisciplineDTO.getDescription());
//        oldDiscipline.setTeachers(teachers);
//        oldDiscipline.setCategories(categories);
//
//        return oldDiscipline;
//    }

}
