package com.cuu.backend.disciplinas_service.Services;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.StudentInscriptionDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.UserDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.Embeddables.Schedule;
import com.cuu.backend.disciplinas_service.Models.Entities.StudentInscription;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.StudentInscriptionRepo;
import com.cuu.backend.disciplinas_service.Services.Interfaces.StudentInscriptionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
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
    private ModelMapper mapper;

    @Override
    public StudentInscriptionDTO createStudentInscription(StudentInscriptionDTO studentInscriptionDTO) {
        //todo validar
        Optional<StudentInscription> inscriptionExists = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineId(studentInscriptionDTO.getStudent().getKeycloakId(), studentInscriptionDTO.getDiscipline().getId());

        if(inscriptionExists.isPresent()){
            //tirar excepcion indicando q el Alumno YA esta inscripto en otra Category de la misma Discipline
            String categoryName = inscriptionExists.get().getCategory().getName();
            String disciplineName = inscriptionExists.get().getDiscipline().getName();
            throw new CustomException("Usted ya está registrado en la Disciplina " + disciplineName + ", en la Categoría " + categoryName + ". Solo puede estar en una sola Categoría de cada Disciplina.", HttpStatus.BAD_REQUEST);

        }

        if (!isInAgeRange(studentInscriptionDTO.getStudent(), studentInscriptionDTO.getCategory())){
            //tirar excepcion indicando q no el usuario no esta dentro del rango de edad de la Category
            long minAge = studentInscriptionDTO.getCategory().getAgeRange().getMinAge();
            long maxAge = studentInscriptionDTO.getCategory().getAgeRange().getMaxAge();
            throw new CustomException("Usted no cumple con el rango de Edad de la Categoría. Mínimo: " + minAge + " | Máximo: " + maxAge, HttpStatus.CONFLICT);

        }

        if (!thereAreAvailablePlaces(studentInscriptionDTO.getCategory())){
            //tirar excepcion indicando q no hay cupos disponibles en esa Category
            throw new CustomException("Ya no hay cupos disponibles en esta Categoría", HttpStatus.CONFLICT);

        }

        Optional<CategoryDTO> clashWithCategoryDTOSchedule = doesNotClashWithOtherSchedules(studentInscriptionDTO.getStudent(), studentInscriptionDTO.getCategory());

        if (clashWithCategoryDTOSchedule.isPresent()){
            //tirar excepcion indicando q otra los horarios (Schedule), de otra Category donde esta inscripto el ALumno,
            // coincide con los horarios de la Category a la cual se quiere inscribir
            String categoryName = clashWithCategoryDTOSchedule.get().getName();
            throw new CustomException("Usted está inscripto en otra Disciplina donde chocan los horarios con la Categoría a la cual se quiere inscribir. Categoría: " + categoryName, HttpStatus.CONFLICT);
        }

        StudentInscription newStudentInscription = mapper.map(studentInscriptionDTO, StudentInscription.class);

        StudentInscription createdStudentInscription = studentInscriptionRepo.save(newStudentInscription);

        return mapper.map(createdStudentInscription, StudentInscriptionDTO.class);
    }

    //se usa SOLO para cambiar de Category a un Alumno, ya q sigue dentro de la misma Discipline.
    // (y como no puede estar en 2 Categories de una misma Discipline al mismo tiempo, se hace UPDATE en la original,
    //  en vez de DELETE la original y CREATE otra nueva actualizada)
    @Override
    public StudentInscriptionDTO updateStudentInscription(StudentInscriptionDTO studentInscriptionDTO) {
        Optional<StudentInscription> oldStudentInscription = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineIdAndCategoryId(studentInscriptionDTO.getStudent().getKeycloakId(), studentInscriptionDTO.getDiscipline().getId(), studentInscriptionDTO.getCategory().getId());

        //todo validar
        if (oldStudentInscription.isEmpty()){
            //tirar excepcion indicando q no el usuario no estaba en otra Category de esa Discipline
            String disciplineName = studentInscriptionDTO.getDiscipline().getName();
            String categoryName = studentInscriptionDTO .getCategory().getName();
            throw new CustomException("Usted NO está registrado en la Disciplina " + disciplineName + ", no lo podemos promover a la Categoría " + categoryName, HttpStatus.BAD_REQUEST);
        }

        StudentInscription updatedStudentInscription = mapper.map(studentInscriptionDTO, StudentInscription.class);
        updatedStudentInscription.setId(oldStudentInscription.get().getId());

        StudentInscription savedStudentInscription = studentInscriptionRepo.save(updatedStudentInscription);

        return mapper.map(savedStudentInscription, StudentInscriptionDTO.class);
    }

    @Override
    public boolean deleteStudentInscription(StudentInscriptionDTO studentInscriptionDTO) {
        Optional<StudentInscription> studentInscription = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineIdAndCategoryId(studentInscriptionDTO.getStudent().getKeycloakId(), studentInscriptionDTO.getDiscipline().getId(), studentInscriptionDTO.getCategory().getId());

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
            studentInscriptionDTOList.add(mapper.map(si, StudentInscriptionDTO.class));

        }

        return studentInscriptionDTOList;
    }

    @Override
    public List<StudentInscriptionDTO> findAllByDisciplineId(UUID disciplineId) {
        List<StudentInscription> studentInscriptions = studentInscriptionRepo.findAllByDisciplineId(disciplineId);

        List<StudentInscriptionDTO> studentInscriptionDTOList = new ArrayList<>();

        for (StudentInscription si : studentInscriptions){
            studentInscriptionDTOList.add(mapper.map(si, StudentInscriptionDTO.class));

        }

        return studentInscriptionDTOList;
    }

    @Override
    public List<StudentInscriptionDTO> findAllByCategoryId(UUID categoryId) {
        List<StudentInscription> studentInscriptions = studentInscriptionRepo.findAllByCategoryId(categoryId);

        List<StudentInscriptionDTO> studentInscriptionDTOList = new ArrayList<>();

        for (StudentInscription si : studentInscriptions){
            studentInscriptionDTOList.add(mapper.map(si, StudentInscriptionDTO.class));

        }

        return studentInscriptionDTOList;
    }

    @Override
    public Optional<StudentInscriptionDTO> findByStudentKeycloakIdAndDisciplineIdAndCategoryId(String studentKeycloakId, UUID disciplineId, UUID categoryId){
        Optional<StudentInscription> studentInscriptionOpt = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineIdAndCategoryId(studentKeycloakId, disciplineId, categoryId);

        if (studentInscriptionOpt.isEmpty()){
            throw new CustomException("No se pudo encontrar StudentInscription con los 3 atributos indicados", HttpStatus.BAD_REQUEST);

        }

        return Optional.of(mapper.map(studentInscriptionOpt.get(), StudentInscriptionDTO.class));
    }

    @Override
    public Optional<StudentInscriptionDTO> findByStudentKeycloakIdAndDisciplineId(String studentKeycloakId, UUID disciplineId) {
        Optional<StudentInscription> studentInscriptionOpt = studentInscriptionRepo.findByStudentKeycloakIdAndDisciplineId(studentKeycloakId, disciplineId);

        if (studentInscriptionOpt.isEmpty()){
            throw new CustomException("No se pudo encontrar StudentInscription con los 2 atributos indicados", HttpStatus.BAD_REQUEST);

        }

        return Optional.of(mapper.map(studentInscriptionOpt.get(), StudentInscriptionDTO.class));
    }

    //metodos de validacion para la inscripcion de un alumno (User) a una Category

    //segun atributo availablePlaces (cupos) de la Category, se verifica si hay espacio o no
    private boolean thereAreAvailablePlaces(CategoryDTO categoryDTO){
        if (categoryDTO.getAvailablePlaces() == null || categoryDTO.getAvailablePlaces() < 1){
            return true;
        }

        long occupiedPlaces = studentInscriptionRepo.countByCategoryId(categoryDTO.getId());

        return categoryDTO.getAvailablePlaces() < occupiedPlaces;
    }

    private boolean isInAgeRange(UserDTO userDTO, CategoryDTO categoryDTO){
        if(categoryDTO.getAgeRange() == null || categoryDTO.getAgeRange().getMinAge() < 0 || categoryDTO.getAgeRange().getMaxAge() < 1){
            return true;
        }

        int userAge = Period.between(userDTO.getBirthDate(), LocalDate.now()).getYears();
        int minAge = categoryDTO.getAgeRange().getMinAge();
        int maxAge = categoryDTO.getAgeRange().getMaxAge();

        return userAge >= minAge && userAge <= maxAge;
    }

    //si coincide con otro schedule, devuelve la Category con la cual choca en horario (Schedule: dia, hora inicio y hora fin)
    // si NO coincide con otra, devuelve optinal vacio
    private Optional<CategoryDTO> doesNotClashWithOtherSchedules(UserDTO userDTO, CategoryDTO categoryDTO) {
        List<StudentInscription> userInscriptions = studentInscriptionRepo.findAllByStudentKeycloakId(userDTO.getKeycloakId());

        if (userInscriptions.isEmpty()) {
            return Optional.empty();
        }

        // todos los horarios del user actual
        List<Schedule> userSchedules = new ArrayList<>();

        for(StudentInscription si : userInscriptions){
            userSchedules.addAll(si.getCategory().getSchedule());
        }

        for (Schedule newSchedule : categoryDTO.getSchedule()) {
            for (Schedule existingSchedule : userSchedules) {
                if (newSchedule.getDayOfWeek() == existingSchedule.getDayOfWeek()
                        && newSchedule.getStartHour().isBefore(existingSchedule.getEndHour())
                        && newSchedule.getEndHour().isAfter(existingSchedule.getStartHour())) {

                    // Encontrar la categoría que tiene ese horario en conflicto
                    List<Category> category = categoryRepo.findBySchedule(
                            existingSchedule.getDayOfWeek(),
                            existingSchedule.getStartHour(),
                            existingSchedule.getEndHour()
                    );

                    if (!category.isEmpty()) {
                        CategoryDTO clashCategory = mapper.map(category.get(0), CategoryDTO.class);
                        return Optional.of(clashCategory);
                    }
                }
            }
        }

        return Optional.empty();
    }
}
