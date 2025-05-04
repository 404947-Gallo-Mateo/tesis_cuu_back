package com.cuu.backend.disciplinas_service.Models.DTOs;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisciplineSummaryDTO {
    private UUID id;
    private String name;
}
