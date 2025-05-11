package com.cuu.backend.disciplinas_service.Models.DTOs.DisplayOnFrontend;

import com.cuu.backend.disciplinas_service.Models.DTOs.CategoryDTO;
import com.cuu.backend.disciplinas_service.Models.DTOs.Summary.DisciplineSummaryDTO;
import com.cuu.backend.disciplinas_service.Models.Enums.Genre;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
//clase para poder mostrar info en el front, haciendo una sola llamada al back
public class ExpandedUserDTO {
    //El campo sub (aca llamado: keycloakId) del token de Keycloak es un identificador unico global por User.
    private String keycloakId;
    private Role role;
    private String username;
    @Email
    private String email;
    private String firstName;
    private String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate birthDate;
    private Genre genre;
    private List<DisciplineSummaryDTO> teacherDisciplines;
    //la idea es mostrar las disciplinas del Alumno en el apartado MisDisciplinas
    private List<CategoryDTO> studentCategories;
}
