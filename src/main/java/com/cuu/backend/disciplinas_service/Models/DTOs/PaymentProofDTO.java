package com.cuu.backend.disciplinas_service.Models.DTOs;

import com.cuu.backend.disciplinas_service.Models.Entities.Fee;
import com.cuu.backend.disciplinas_service.Models.Enums.PaymentType;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProofDTO {

    private String userKeycloakId;

    private LocalDateTime paymentDate;

    private String transactionId; // ID de transacción de Mercado Pago

    private PaymentType paymentMethod; // Ej: "credit_card", "debit_card", "mercado_pago"

    private String paymentProofUrl;

    private String status; // "approved", "pending", "rejected"

    private String payerEmail; // Email del pagador
}
