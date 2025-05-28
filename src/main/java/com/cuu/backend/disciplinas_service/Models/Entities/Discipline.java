package com.cuu.backend.disciplinas_service.Models.Entities;

import com.cuu.backend.disciplinas_service.Models.Entities.Embeddables.Schedule;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

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
@Table(name = "disciplines")
public class Discipline {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator()
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(length = 1500)
    private String description;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "discipline_teachers",
            joinColumns = @JoinColumn(name = "discipline_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private List<User> teachers;
    @OneToMany(mappedBy = "discipline", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Category> categories;
}
