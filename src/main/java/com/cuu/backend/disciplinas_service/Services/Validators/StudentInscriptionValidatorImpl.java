package com.cuu.backend.disciplinas_service.Services.Validators;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.UserDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Entities.Embeddables.Schedule;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.DisciplineRepo;
import com.cuu.backend.disciplinas_service.Repositories.StudentInscriptionRepo;
import com.cuu.backend.disciplinas_service.Repositories.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentInscriptionValidatorImpl {

    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private StudentInscriptionRepo studentInscriptionRepo;
    @Autowired
    private DisciplineRepo disciplineRepo;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper mapper;


    public StudentInscription validatePostStudentInscriptionDTO(StudentInscriptionDTO studentInscriptionDTO) {
        Optional<StudentInscription> inscriptionExists = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineId(studentInscriptionDTO.getStudent().getKeycloakId(), studentInscriptionDTO.getDiscipline().getId());

        if(inscriptionExists.isPresent()){
            //tirar excepcion indicando q el Alumno YA esta inscripto en otra Category de la misma Discipline
            String categoryName = inscriptionExists.get().getCategory().getName();
            String disciplineName = inscriptionExists.get().getDiscipline().getName();
            throw new CustomException("Usted ya está registrado en la Disciplina " + disciplineName + ", en la Categoría " + categoryName + ". Solo puede estar en una sola Categoría de cada Disciplina.", HttpStatus.BAD_REQUEST);
        }

        Optional<User> student = userRepo.findByKeycloakId(studentInscriptionDTO.getStudent().getKeycloakId());
        Optional<Discipline> discipline = disciplineRepo.findById(studentInscriptionDTO.getDiscipline().getId());
        Optional<Category> category = categoryRepo.findById(studentInscriptionDTO.getCategory().getId());

        if (student.isEmpty() || discipline.isEmpty() || category.isEmpty()) {
            throw new CustomException("1 o mas entidades no existen (false no existe), student: " + student.isPresent() + " | discipline: " + discipline.isPresent() + " | category: " + category.isPresent(), HttpStatus.BAD_REQUEST);
        }

        StudentInscription newStudentInscription = new StudentInscription(null, student.get(), discipline.get(), category.get());

        //valida q la Category pertenece a la Discipline
        if (!newStudentInscription.getCategory().getDiscipline().getId().equals(newStudentInscription.getDiscipline().getId())){
            throw new CustomException("La nueva Category " + newStudentInscription.getCategory().getName() + "no es parte de la Discipline" + newStudentInscription.getDiscipline().getName(), HttpStatus.BAD_REQUEST);
        }

        if (!isInAgeRange(student.get(), category.get())){
            //tirar excepcion indicando q no el usuario no esta dentro del rango de edad de la Category
            long minAge = category.get().getAgeRange().getMinAge();
            long maxAge = category.get().getAgeRange().getMaxAge();
            throw new CustomException("Usted no cumple con el rango de Edad de la Categoría. Mínimo: " + minAge + " | Máximo: " + maxAge, HttpStatus.CONFLICT);

        }

        if (!thereAreAvailablePlaces(category.get())){
            //tirar excepcion indicando q no hay cupos disponibles en esa Category
            throw new CustomException("Ya no hay cupos disponibles en esta Categoría", HttpStatus.CONFLICT);

        }

        Optional<CategoryDTO> clashWithCategoryDTOSchedule = doesNotClashWithOtherSchedules(studentInscriptionDTO.getStudent(), category.get());

        if (clashWithCategoryDTOSchedule.isPresent()){
            //tirar excepcion indicando los horarios (Schedule), de otra Category donde esta inscripto el Alumno,
            // coincide con los horarios de la Category a la cual se quiere inscribir
            String categoryName = clashWithCategoryDTOSchedule.get().getName();
            throw new CustomException("Usted está inscripto en otra Disciplina donde chocan los horarios con la Categoría a la cual se quiere inscribir. Categoría: " + categoryName, HttpStatus.CONFLICT);
        }

        return newStudentInscription;
    }

    public StudentInscription validatePutStudentInscriptionDTO(StudentInscriptionDTO studentInscriptionDTO){
        Optional<StudentInscription> oldStudentInscriptionOpt = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineId(studentInscriptionDTO.getStudent().getKeycloakId(), studentInscriptionDTO.getDiscipline().getId());

        if (oldStudentInscriptionOpt.isEmpty()){
            //tirar excepcion indicando q no el usuario NO estaba en otra Category de esa Discipline
            String disciplineName = studentInscriptionDTO.getDiscipline().getName();
            String categoryName = studentInscriptionDTO .getCategory().getName();
            throw new CustomException("Usted NO está registrado en la Disciplina " + disciplineName + ", no lo podemos promover a la Categoría " + categoryName, HttpStatus.BAD_REQUEST);
        }

        UUID oldCategoryId = oldStudentInscriptionOpt.get().getCategory().getId();

        Optional<User> student = userRepo.findByKeycloakId(studentInscriptionDTO.getStudent().getKeycloakId());
        Optional<Discipline> discipline = disciplineRepo.findById(studentInscriptionDTO.getDiscipline().getId());
        Optional<Category> category = categoryRepo.findById(studentInscriptionDTO.getCategory().getId());

        //verifica q todas las entities ya existan en la db
        if (student.isEmpty() || discipline.isEmpty() || category.isEmpty()) {
            throw new CustomException("1 o mas entidades no existen (false no existe), student: " + student.isPresent() + " | discipline: " + discipline.isPresent() + " | category: " + category.isPresent(), HttpStatus.BAD_REQUEST);
        }

        StudentInscription updatedStudentInscription = oldStudentInscriptionOpt.get();
        updatedStudentInscription.setStudent(student.get());
        updatedStudentInscription.setDiscipline(discipline.get());
        updatedStudentInscription.setCategory(category.get());

        //valida q haya un cambio de Category
        if (oldCategoryId.equals(updatedStudentInscription.getCategory().getId())){
            throw new CustomException("La Category vieja y nueva tienen el mismo ID, no hay nada que actualizar ya que el Alumno no cambio de Category", HttpStatus.BAD_REQUEST);
        }

        //valida q la Category pertenece a la Discipline
        if (!updatedStudentInscription.getCategory().getDiscipline().getId().equals(updatedStudentInscription.getDiscipline().getId())){
            throw new CustomException("La nueva Category " + updatedStudentInscription.getCategory().getName() + " no es parte de la Discipline " + updatedStudentInscription.getDiscipline().getName(), HttpStatus.BAD_REQUEST);
        }

        if (!isInAgeRange(updatedStudentInscription.getStudent(), updatedStudentInscription.getCategory())){
            //tirar excepcion indicando q no el usuario no esta dentro del rango de edad de la Category
            long minAge = updatedStudentInscription.getCategory().getAgeRange().getMinAge();
            long maxAge = updatedStudentInscription.getCategory().getAgeRange().getMaxAge();
            throw new CustomException("Usted no cumple con el rango de Edad de la Categoría. Mínimo: " + minAge + " | Máximo: " + maxAge, HttpStatus.CONFLICT);

        }

        if (!thereAreAvailablePlaces(updatedStudentInscription.getCategory())){
            //tirar excepcion indicando q no hay cupos disponibles en esa Category
            throw new CustomException("Ya no hay cupos disponibles en esta Categoría", HttpStatus.CONFLICT);

        }

        Optional<CategoryDTO> clashWithCategoryDTOSchedule = doesNotClashWithOtherSchedules(studentInscriptionDTO.getStudent(), updatedStudentInscription.getCategory());

        if (clashWithCategoryDTOSchedule.isPresent()){
            //tirar excepcion indicando los horarios (Schedule), de otra Category donde esta inscripto el Alumno,
            // coincide con los horarios de la Category a la cual se quiere inscribir
            String categoryName = clashWithCategoryDTOSchedule.get().getName();
            String disciplineName = clashWithCategoryDTOSchedule.get().getDisciplineName();

            throw new CustomException("Usted está inscripto en otra Disciplina donde chocan los horarios con la Categoría a la cual se quiere inscribir. la otra Disciplina y Categoría: " + disciplineName + ", " + categoryName, HttpStatus.CONFLICT);
        }

        return updatedStudentInscription;
    }

    //metodos de validacion para la inscripcion de un alumno (User) a una Category

    //segun atributo availablePlaces (cupos) de la Category, se verifica si hay espacio o no
    private boolean thereAreAvailablePlaces(Category category){
        if (category.getAvailableSpaces() == null || category.getAvailableSpaces() < 1){
            return true;
        }

        long occupiedPlaces = studentInscriptionRepo.countByCategoryId(category.getId());

        return category.getAvailableSpaces() < occupiedPlaces;
    }

    private boolean isInAgeRange(User user, Category category){
        if(category.getAgeRange() == null || category.getAgeRange().getMinAge() < 0 || category.getAgeRange().getMaxAge() < 1){
            return true;
        }

        int userAge = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        int minAge = category.getAgeRange().getMinAge();
        int maxAge = category.getAgeRange().getMaxAge();

        return userAge >= minAge && userAge <= maxAge;
    }

    //si coincide con otro schedule, devuelve la Category con la cual choca en horario (Schedule: dia, hora inicio y hora fin)
    // si NO coincide con otra, devuelve optinal vacio
    private Optional<CategoryDTO> doesNotClashWithOtherSchedules(UserDTO userDTO, Category newCategory) {
        List<StudentInscription> userInscriptions = studentInscriptionRepo.findAllByStudentKeycloakId(userDTO.getKeycloakId());

        if (userInscriptions.isEmpty()) {
            return Optional.empty();
        }

        // todos los horarios del user actual
        List<Schedule> userSchedules = new ArrayList<>();

        for(StudentInscription si : userInscriptions){
            //NO agrega la List<Schedule> de la vieja Category q esta reemplazando la NUEVA Category
            if (!si.getDiscipline().getId().equals(newCategory.getDiscipline().getId())){
                userSchedules.addAll(si.getCategory().getSchedule());
            }
        }

        for (Schedule newSchedule : newCategory.getSchedule()) {
            for (Schedule existingSchedule : userSchedules) {
                if (newSchedule.getDayOfWeek() == existingSchedule.getDayOfWeek()
                        && newSchedule.getStartHour().isBefore(existingSchedule.getEndHour())
                        && newSchedule.getEndHour().isAfter(existingSchedule.getStartHour())) {

                    // Encontrar la categoría que tiene ese horario en conflicto
                    List<Category> categories = categoryRepo.findBySchedule(
                            existingSchedule.getDayOfWeek(),
                            existingSchedule.getStartHour(),
                            existingSchedule.getEndHour()
                    );

                    if (!categories.isEmpty()) {
                        CategoryDTO clashCategory = mapper.map(categories.get(0), CategoryDTO.class);
                        return Optional.of(clashCategory);
                    }
                }
            }
        }

        return Optional.empty();
    }

}
