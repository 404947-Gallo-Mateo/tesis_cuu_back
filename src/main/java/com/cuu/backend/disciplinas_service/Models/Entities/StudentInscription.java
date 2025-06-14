package com.cuu.backend.disciplinas_service.Models.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "student_inscriptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "discipline_id"}))
public class StudentInscription {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator()
    private UUID id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    @ManyToOne(optional = false)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;
    @Column(name = "updated_date", nullable = true)
    private LocalDate updatedDate;
}
