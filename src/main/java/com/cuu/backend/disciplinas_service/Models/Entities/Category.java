package com.cuu.backend.disciplinas_service.Models.Entities;

import com.cuu.backend.disciplinas_service.Models.Entities.Embeddables.AgeRange;
import com.cuu.backend.disciplinas_service.Models.Entities.Embeddables.Schedule;
import com.cuu.backend.disciplinas_service.Models.Enums.AllowedGenre;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator()
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1500)
    private String description;

    @Column(nullable = false, name = "monthly_fee")
    private BigDecimal monthlyFee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;

    @Column(nullable = true, name = "available_spaces")
    private Long availableSpaces;

    @Embedded
    private AgeRange ageRange;

    @ElementCollection
    @CollectionTable(
            name = "category_schedule",
            joinColumns = @JoinColumn(name = "category_id")
    )
    private List<Schedule> schedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "allowed_genre")
    private AllowedGenre allowedGenre;
}
