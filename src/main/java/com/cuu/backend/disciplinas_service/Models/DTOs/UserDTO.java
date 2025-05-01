package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.Entities.Discipline;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserDTO {
    //El campo sub (aca llamado: keycloakId) del token de Keycloak es un identificador unico global por User.
    //@Column(nullable = false, unique = true)
    private String keycloakId;

    @Enumerated(EnumType.STRING)
    //@Column(nullable = false)
    private List<Role> roles;

    //@Column(nullable = false, unique = true)
    private String username;

    @Email
    //@Column(nullable = false, unique = true)
    private String email;

    //@Column(nullable = false)
    private String firstName;

    //@Column(nullable = false)
    private String lastName;

    private List<DisciplineDTO> teacherDisciplines;

}
