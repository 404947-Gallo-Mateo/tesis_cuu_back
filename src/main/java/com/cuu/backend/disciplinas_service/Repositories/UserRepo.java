package com.cuu.backend.disciplinas_service.Repositories;

import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiDebtorsQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    @Query("SELECT COUNT(DISTINCT u.id) " +
            "FROM User u " +
            "JOIN u.fees f " +
            "WHERE f.disciplineId = :disciplineId " +
            "AND f.dueDate < CURRENT_DATE " +
            "AND f.paymentDate IS NULL")
    Long countDisciplineDebtors(@Param("disciplineId") UUID disciplineId);
    @Query("SELECT COUNT(DISTINCT u.id) " +
            "FROM User u " +
            "JOIN u.fees f " +
            "WHERE f.disciplineId = :disciplineId " +
            "AND (f.dueDate >= CURRENT_DATE OR f.paymentDate IS NOT NULL)")
    Long countDisciplineUpToDateUsers(@Param("disciplineId") UUID disciplineId);


    @Query("SELECT COUNT(DISTINCT u.id) " +
            "FROM User u " +
            "JOIN u.fees f " +
            "WHERE f.feeType = 'SOCIAL' " +
            "AND f.dueDate < CURRENT_DATE " +
            "AND f.paymentDate IS NULL")
    Long countSocialDebtors();
    @Query("SELECT COUNT(DISTINCT u.id) " +
            "FROM User u " +
            "JOIN u.fees f " +
            "WHERE f.feeType = 'SOCIAL' " +
            "AND (f.dueDate >= CURRENT_DATE OR f.paymentDate IS NOT NULL)")
    Long countSocialUpToDateUsers();

    @Query("SELECT COUNT(u) FROM User u")
    long countUsers();
    @Query("SELECT COUNT(u) FROM User u WHERE u.genre = 'MALE' ")
    long countMaleUsers();
    @Query("SELECT COUNT(u) FROM User u WHERE u.genre = 'FEMALE' ")
    long countFemaleUsers();
    @Query("SELECT u FROM User u WHERE u.keycloakId = :keycloakId")
    Optional<User> findByKeycloakId(@Param("keycloakId") String keycloakId);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT DISTINCT u FROM User u " +
            "ORDER BY u.lastName ASC, u.firstName ASC")
    List<User> getAllOrdered();
    @Query("SELECT DISTINCT u FROM User u " +
            "Where u.role = :role " +
            "ORDER BY u.lastName ASC, u.firstName ASC")
    List<User> getAllByRoleOrdered(@Param("role")Role role);

    Optional<User> findIdByKeycloakId(String keycloakId);
}
