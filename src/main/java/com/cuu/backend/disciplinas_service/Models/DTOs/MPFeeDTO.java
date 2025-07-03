package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.Enums.FeeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MPFeeDTO {
    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    private String period; // Período que cubre la cuota

    private String userKeycloakId;

    private UUID disciplineId; // Nullable para cuotas sociales

}
