package com.cuu.backend.disciplinas_service.Services.Validators;

import com.cuu.backend.disciplinas_service.Controllers.ManageExceptions.CustomException;
import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.DisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.ICategorySummary;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostCategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PostDisciplineDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.forPost.PutCategoryDTO;
import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Entities.Embeddables.Schedule;
import com.cuu.backend.disciplinas_service.Models.Enums.Genre;
import com.cuu.backend.disciplinas_service.Repositories.CategoryRepo;
import com.cuu.backend.disciplinas_service.Repositories.DisciplineRepo;
import com.cuu.backend.disciplinas_service.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryValidatorImpl {

    @Autowired
    private DisciplineRepo disciplineRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CategoryRepo categoryRepo;

    public void validatePostCategoryDTO(List<PostCategoryDTO> listDto) {
        for (PostCategoryDTO dto : listDto){
            if (dto.getName().length() > 100){
                throw new CustomException("El nombre de la Categoría no debe superar los 100 caracteres", HttpStatus.BAD_REQUEST);
            }
            if (dto.getDescription().length() > 1000){
                throw new CustomException("La descripción de la Categoría no debe superar los 1000 caracteres", HttpStatus.BAD_REQUEST);
            }
            if (dto.getMonthlyFee().compareTo(BigDecimal.ONE) < 0){
                throw new CustomException("El Valor, de la Cuota de la Categoría, no debe ser menor que $1", HttpStatus.BAD_REQUEST);
            }
            if (dto.getAgeRange().getMinAge() > dto.getAgeRange().getMaxAge()){
                throw new CustomException("La edad mínima, del Rango de Edad de la Categoría, no puede ser mayor que la edad máxima", HttpStatus.BAD_REQUEST);
            }
            for (Schedule s : dto.getSchedules()){
                if (s.getStartHour().isAfter(s.getEndHour())){
                    throw new CustomException("La Hora de Inicio, del Horario día" + s.getDayOfWeek() + ", no puede ser mayor que la Hora de Fin", HttpStatus.BAD_REQUEST);
                }
            }
            if (dto.getAllowedGenre() != Genre.MALE && dto.getAllowedGenre() != Genre.FEMALE && dto.getAllowedGenre() != Genre.MIXED){
                throw new CustomException("El Género permitido, de la Categoría, debe ser Masculino, Femenino o Mixto", HttpStatus.BAD_REQUEST);
            }
        }
    }

    public void validatePutCategoryDTO(List<PutCategoryDTO> listDto) {

        for (PutCategoryDTO dto : listDto){

                if (dto.getName().length() > 100){
                    throw new CustomException("El nombre de la Categoría no debe superar los 100 caracteres", HttpStatus.BAD_REQUEST);
                }
                if (dto.getDescription().length() > 1000){
                    throw new CustomException("La descripción de la Categoría no debe superar los 1000 caracteres", HttpStatus.BAD_REQUEST);
                }
                if (dto.getMonthlyFee().compareTo(BigDecimal.ONE) < 0){
                    throw new CustomException("El valor de la cuota de la Categoría no debe ser menor que $1", HttpStatus.BAD_REQUEST);
                }
                if (dto.getAgeRange().getMinAge() > dto.getAgeRange().getMaxAge()){
                    throw new CustomException("La edad mínima, del Rango de Edad de la Categoría, no puede ser mayor que la edad máxima", HttpStatus.BAD_REQUEST);
                }
                for (Schedule s : dto.getSchedules()){
                    if (s.getStartHour().isAfter(s.getEndHour())){
                        throw new CustomException("La Hora de Inicio, del Horario día" + s.getDayOfWeek() + ", no puede ser mayor que la Hora de Fin", HttpStatus.BAD_REQUEST);
                    }
                }
                if (dto.getAllowedGenre() != Genre.MALE && dto.getAllowedGenre() != Genre.FEMALE && dto.getAllowedGenre() != Genre.MIXED){
                    throw new CustomException("El Género permitido, de la Categoría, debe ser Masculino, Femenino o Mixto", HttpStatus.BAD_REQUEST);
                }
        }
    }
}
