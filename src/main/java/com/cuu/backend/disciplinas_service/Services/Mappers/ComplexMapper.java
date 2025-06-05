package com.cuu.backend.disciplinas_service.Services.Mappers;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.*;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.DisciplineSummaryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.ICategorySummary;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostCategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostDisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PutCategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PutDisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.*;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.DisciplineRepo;
import com.cuu.backend.disciplinas_service.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public FeeDTO mapFeeEntityToFeeDTO(Fee fee){
        FeeDTO feeDTO = new FeeDTO(fee.getFeeType(), fee.getAmount(), fee.getDueDate(), fee.getPeriod(), null, fee.getUserKeycloakId(), fee.getPayerEmail(), fee.getDisciplineId(), fee.isPaid(), null, fee.getCreatedAt(), fee.getDescription());

        User feeUser = fee.getUser();
        List<DisciplineSummaryDTO> userDTOTeacherDisciplines = this.getDisciplineSummaryDTOListFromTeacherUser(feeUser.getTeacherDisciplines());

        UserDTO feeDTOUserDTO = new UserDTO();
        if (fee.getUser() != null){
            feeDTOUserDTO = new UserDTO(feeUser.getKeycloakId(), feeUser.getRole(), feeUser.getUsername(), feeUser.getEmail(), feeUser.getFirstName(), feeUser.getLastName(), feeUser.getBirthDate(), feeUser.getGenre(), userDTOTeacherDisciplines);
        }

        if (fee.getPaymentProof() != null){
            PaymentProof feePaymentProof = fee.getPaymentProof();
            PaymentProofDTO paymentProofDTO = new PaymentProofDTO(feeDTO, feePaymentProof.getUserKeycloakId(), feePaymentProof.getPaymentDate(), feePaymentProof.getTransactionId(), feePaymentProof.getPaymentMethod(), feePaymentProof.getPaymentProofUrl(), feePaymentProof.getStatus(), feePaymentProof.getPayerEmail());

            feeDTO.setPaymentProof(paymentProofDTO);
        }

        feeDTO.setUser(feeDTOUserDTO);
        return feeDTO;
    }

    public DisciplineDTO mapDisciplineEntityToDisciplineDTO(Discipline discipline){
        List<ICategorySummary> categoriesDTOList = this.getCategoriesDTOFromDiscipline(discipline.getCategories());
        List<UserDTO> teachersList = this.getTeachersDTOFromDiscipline(discipline.getTeachers());

        return new DisciplineDTO(discipline.getId(), discipline.getName(), discipline.getDescription(), teachersList, categoriesDTOList);
    }

    private List<ICategorySummary> getCategoriesDTOFromDiscipline(List<Category> disciplineCategories){
        List<ICategorySummary> categoriesDTOList = new ArrayList<>();

        for (Category c : disciplineCategories){
            CategoryDTO dto = new CategoryDTO(c.getId(), c.getName(), c.getDescription(), c.getMonthlyFee(), c.getDiscipline().getId(), c.getDiscipline().getName(), c.getAvailableSpaces(), c.getAgeRange(), c.getSchedules(), c.getAllowedGenre());
            categoriesDTOList.add(dto);
        }

        return categoriesDTOList;
    }

    private List<UserDTO> getTeachersDTOFromDiscipline(List<User> disciplineTeachers){
        List<UserDTO> teachersDTOList = new ArrayList<>();

        for (User u : disciplineTeachers){
            List<DisciplineSummaryDTO> teacherDisciplineSummaryDTOList = getDisciplineSummaryDTOListFromTeacherUser(u.getTeacherDisciplines());

            UserDTO teacherDTO = new UserDTO(u.getKeycloakId(), u.getRole(), u.getUsername(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getBirthDate(), u.getGenre(), teacherDisciplineSummaryDTOList);
            teachersDTOList.add(teacherDTO);
        }
        return teachersDTOList;
    }

    private List<DisciplineSummaryDTO> getDisciplineSummaryDTOListFromTeacherUser(List<Discipline> teacherDisciplines){
        List<DisciplineSummaryDTO> disciplineSummaryDTOList = new ArrayList<>();

        if (!teacherDisciplines.isEmpty()){
            for (Discipline d : teacherDisciplines){
                DisciplineSummaryDTO summaryDTO = new DisciplineSummaryDTO(d.getId(), d.getName());
                disciplineSummaryDTOList.add(summaryDTO);
            }
        }

        return disciplineSummaryDTOList;
    }

    public Discipline mapPostDTOToDiscipline(PostDisciplineDTO postDTO) {
        Discipline newDiscipline = new Discipline();
        newDiscipline.setName(postDTO.getName());
        newDiscipline.setDescription(postDTO.getDescription());

        newDiscipline = disciplineRepo.save(newDiscipline);
//      obteniendo teachers
        List<User> teachers = this.getTeachersFromPostDiscipline(postDTO, newDiscipline);

//      obteniendo categories
        List<Category> newCategories = this.getCategoriesFromPostDiscipline(postDTO, newDiscipline);

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
                    postCategoryDTO.getSchedules(),
                    postCategoryDTO.getAllowedGenre()
            );

            categories.add(category);
        }

        return categories;
    }

    private List<User> getTeachersFromPostDiscipline(PostDisciplineDTO postDTO, Discipline newDiscipline){
        List<User> teachers = new ArrayList<>();

        for (String teacherKeycloakId : postDTO.getTeacherIds()) {
            Optional<User> teacherOpt = userRepo.findByKeycloakId(teacherKeycloakId);

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
        }
        return teachers;
    }

    public Discipline mapDisciplineDTOToDiscipline(PutDisciplineDTO updatedDisciplineDTO, Discipline oldDiscipline) {
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

    private List<Category> getCategoriesFromDiscipline(PutDisciplineDTO updatedDisciplineDTO, Discipline oldDiscipline) {
        List<Category> categories = new ArrayList<>();

        for (PutCategoryDTO updatedcategory : updatedDisciplineDTO.getCategories()) {
            Optional<Category> optionalCategory = categoryRepo.findById(updatedcategory.getId());

                Category category;

                if (optionalCategory.isPresent()) {
                    category = optionalCategory.get();
                    // actualiza sus valores
                    category.setName(updatedcategory.getName());
                    category.setDescription(updatedcategory.getDescription());
                    category.setMonthlyFee(updatedcategory.getMonthlyFee());
                    category.setAvailableSpaces(updatedcategory.getAvailableSpaces());
                    category.setAgeRange(updatedcategory.getAgeRange());
                    category.setSchedules(updatedcategory.getSchedules());
                    category.setAllowedGenre(updatedcategory.getAllowedGenre());
                } else {
                    category = new Category(
                            null,
                            updatedcategory.getName(),
                            updatedcategory.getDescription(),
                            updatedcategory.getMonthlyFee(),
                            oldDiscipline,
                            updatedcategory.getAvailableSpaces(),
                            updatedcategory.getAgeRange(),
                            updatedcategory.getSchedules(),
                            updatedcategory.getAllowedGenre()
                    );
                }

                categories.add(category);
        }

        return categories;
    }

    private List<User> getTeachersFromDiscipline(PutDisciplineDTO updatedDisciplineDTO, Discipline oldDiscipline) {
        List<User> teachers = new ArrayList<>();

        for (String teacherKeycloakId : updatedDisciplineDTO.getTeacherIds()) {
            Optional<User> teacherOpt = userRepo.findByKeycloakId(teacherKeycloakId);

            if (teacherOpt.isPresent()) {

                User existingTeacher = teacherOpt.get();

                if (existingTeacher.getRole().equals(Role.TEACHER)){
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
            }
        }
        return teachers;
    }
}
