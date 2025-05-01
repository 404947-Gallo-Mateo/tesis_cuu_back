package com.cuu.backend.disciplinas_service.Models.Entities;

import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator()
    private UUID id;

    //El campo sub (aca llamado: keycloakId) del token de Keycloak es un identificador unico global por User.
    @Column(nullable = false, unique = true)
    private String keycloakId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private List<Role> roles;

    @Column(nullable = false, unique = true)
    private String username;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToMany
    @JoinTable(
            name = "teacher_disciplines",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "discipline_id")
    )
    private List<Discipline> teacherDisciplines;
}
