package com.cuu.backend.disciplinas_service.Entities;

import com.cuu.backend.disciplinas_service.Enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.net.PasswordAuthentication;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, unique = true)
    private String username;

    private String firstName;

    private String lastName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    private boolean active;

    @ManyToMany
    @JoinTable(
            name = "teacher_disciplines",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "discipline_id")
    )
    private List<Discipline> teacherDisciplines;
}
