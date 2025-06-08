package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.Entities.PaymentProof;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Models.Enums.FeeType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeDTO {
    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    private BigDecimal amount;

    private LocalDate dueDate; // Fecha de vencimiento
    private boolean isDue;

    private String period; // Período que cubre la cuota

    private UserDTO user;
    // campos de respaldo (importante para el historial)
    private String userKeycloakId;
    private String payerEmail; // Email del pagador

    private UUID disciplineId; // Nullable para cuotas sociales
    private UUID categoryId;

    private String disciplineName;
    private String categoryName;

    private boolean paid = false;

    private PaymentProofDTO paymentProof;

    private String createdAt; // es LocalDateTime en Fee

    private String description;
}
