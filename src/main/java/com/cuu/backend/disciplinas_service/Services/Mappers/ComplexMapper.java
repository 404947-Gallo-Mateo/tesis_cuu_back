package com.cuu.backend.disciplinas_service.Services.Mappers;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ComplexMapper {

    @Autowired
    private DisciplineRepo disciplineRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CategoryRepo categoryRepo;

    public Discipline mapPostDTOToDiscipline(PostDisciplineDTO postDTO) {
        Discipline newDiscipline = new Discipline();
        newDiscipline.setName(postDTO.getName());
        newDiscipline.setDescription(postDTO.getDescription());

        newDiscipline = disciplineRepo.save(newDiscipline);
//      obteniendo teachers
        List<User> teachers = getTeachersFromPostDiscipline(postDTO, newDiscipline);

//      obteniendo categories
        List<Category> newCategories = getCategoriesFromPostDiscipline(postDTO, newDiscipline);

//      creando nueva Discipline
        newDiscipline.setTeachers(teachers);
        newDiscipline.setCategories(newCategories);

        return newDiscipline;
    }

    private List<Category> getCategoriesFromPostDiscipline(PostDisciplineDTO postDTO, Discipline newDiscipline){
        List<Category> categories = new ArrayList<>();

        for (PostCategoryDTO postCategoryDTO : postDTO.getCategories()) {

            Category category = new Category(
                    null,
                    postCategoryDTO.getName(),
                    postCategoryDTO.getDescription(),
                    postCategoryDTO.getMonthlyFee(),
                    newDiscipline,
                    postCategoryDTO.getAvailableSpaces(),
                    postCategoryDTO.getAgeRange(),
                    postCategoryDTO.getSchedule(),
                    postCategoryDTO.getAllowedGenre()
            );

            categories.add(category);
        }

        return categories;
    }

    private List<User> getTeachersFromPostDiscipline(PostDisciplineDTO postDTO, Discipline newDiscipline){
        List<User> teachers = new ArrayList<>();

        for (UserDTO teacherDTO : postDTO.getTeachers()) {
            Optional<User> teacherOpt = userRepo.findByKeycloakId(teacherDTO.getKeycloakId());

            if (teacherOpt.isPresent()) {
                User existingTeacher = teacherOpt.get();

                if (existingTeacher.getTeacherDisciplines() == null) {
                    existingTeacher.setTeacherDisciplines(new ArrayList<>());
                }

                boolean alreadyAssigned = existingTeacher.getTeacherDisciplines().stream()
                        .anyMatch(d -> d.getId() != null && d.getId().equals(newDiscipline.getId()));

                if (!alreadyAssigned) {
                    existingTeacher.getTeacherDisciplines().add(newDiscipline);
                }

                teachers.add(existingTeacher);
            }
            else {
                // nuevo User profesor
                List<Discipline> teacherDisciplines = new ArrayList<>();
                teacherDisciplines.add(newDiscipline);

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

                userRepo.save(newTeacher);
                teachers.add(newTeacher);
            }
        }
        return teachers;
    }

    public Discipline mapDisciplineDTOToDiscipline(DisciplineDTO updatedDisciplineDTO, Discipline oldDiscipline) {
        List<User> teachers = getTeachersFromDiscipline(updatedDisciplineDTO, oldDiscipline);

        List<Category> newCategories = getCategoriesFromDiscipline(updatedDisciplineDTO, oldDiscipline);

        // saca Categorias que ya no estan en el DTO actualizado
        oldDiscipline.getCategories().removeIf(oldCat ->
                newCategories.stream().noneMatch(newCat -> newCat.getId() != null && newCat.getId().equals(oldCat.getId()))
        );

        // agrega o actualiza Categorias
        for (Category newCategory : newCategories) {
            newCategory.setDiscipline(oldDiscipline); // asegura la relacion inversa

            if (!oldDiscipline.getCategories().contains(newCategory)) {
                oldDiscipline.getCategories().add(newCategory);
            }
        }

        oldDiscipline.setName(updatedDisciplineDTO.getName());
        oldDiscipline.setDescription(updatedDisciplineDTO.getDescription());
        oldDiscipline.setTeachers(teachers);

        return oldDiscipline;
    }

    private List<Category> getCategoriesFromDiscipline(DisciplineDTO updatedDisciplineDTO, Discipline oldDiscipline) {
        List<Category> categories = new ArrayList<>();

        for (CategoryDTO categoryDTO : updatedDisciplineDTO.getCategories()) {
            Optional<Category> optionalCategory = categoryRepo.findById(categoryDTO.getId());

            Category category;
            if (optionalCategory.isPresent()) {
                category = optionalCategory.get();
                // actualiza sus valores
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
                // nuevo User profesor
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

                userRepo.save(newTeacher);
                teachers.add(newTeacher);
            }
        }
        return teachers;
    }
}
