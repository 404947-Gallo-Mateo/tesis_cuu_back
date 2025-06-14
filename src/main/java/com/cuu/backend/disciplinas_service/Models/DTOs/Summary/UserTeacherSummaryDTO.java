package com.cuu.backend.disciplinas_service.Models.DTOs.Summary;

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
public class UserTeacherSummaryDTO {
    private String keycloakId;
    @Email
    private String email;
    private String firstName;
    private String lastName;
}
