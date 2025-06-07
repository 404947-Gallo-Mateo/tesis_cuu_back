package com.cuu.backend.disciplinas_service.Models.Entities;

import com.cuu.backend.disciplinas_service.Models.Enums.FeeType;
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
@Table(name = "fees")
public class Fee {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator()
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate dueDate; // Fecha de vencimiento

    @Column(nullable = false)
    private YearMonth period; // Período que cubre la cuota

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    // campos de respaldo (importante para el historial)
    @Column(nullable = false)
    private String userKeycloakId;
    @Column
    private String payerEmail; // Email del pagador

    @Column
    private UUID disciplineId; // Nullable para cuotas sociales
    @Column
    private UUID categoryId;
    @Column(nullable = false)
    private boolean paid = false;

    @OneToOne(mappedBy = "fee", cascade = CascadeType.ALL)
    private PaymentProof paymentProof;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private String description; // Ej: "Cuota social Enero 2024" o "Natación - Marzo 2024"
}
