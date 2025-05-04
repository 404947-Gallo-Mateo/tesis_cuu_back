package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.Enums.Genre;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDTO {
    //El campo sub (aca llamado: keycloakId) del token de Keycloak es un identificador unico global por User.
    private String keycloakId;
    private Role role;
    private String username;
    @Email
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Genre genre;
    private List<DisciplineSummaryDTO> teacherDisciplines;

}
