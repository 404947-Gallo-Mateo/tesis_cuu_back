package com.cuu.backend.disciplinas_service.Models.DTOs.Summary;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisciplineSummaryDTO implements IDisciplineSummary{
    private UUID id;
    private String name;
}
