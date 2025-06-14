package com.cuu.backend.disciplinas_service.Models.Entities;

import com.cuu.backend.disciplinas_service.Models.Enums.PaymentType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

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
@Entity
@Table(name = "payment_proof")
public class PaymentProof {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator()
    private UUID id;

    @OneToOne
    @JoinColumn(name = "fee_id", nullable = false)
    private Fee fee;

    @Column(nullable = false)
    private String userKeycloakId;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column
    private String transactionId; // ID de transacción de Mercado Pago

    @Column
    private PaymentType paymentMethod; // Ej: "credit_card", "debit_card", "mercado_pago"

    @Column
    private String paymentProofUrl;

    @Column
    private String status; // "approved", "pending", "rejected"

    @Column
    private String payerEmail; // Email del pagador
}
