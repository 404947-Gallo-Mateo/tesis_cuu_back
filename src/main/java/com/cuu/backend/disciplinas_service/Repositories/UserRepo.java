package com.cuu.backend.disciplinas_service.Repositories;

import com.cuu.backend.disciplinas_service.Models.Entities.Category;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Models.Enums.Role;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiDebtorsQuantity;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    // queries para KPI Users
    @Query("SELECT COUNT(u) FROM User u " +
            "WHERE u.createdDate BETWEEN :start AND :end")
    long countUsers(@Param("start") LocalDate start, @Param("end")  LocalDate end);
    @Query("SELECT COUNT(u) FROM User u " +
            "WHERE u.genre = 'MALE' " +
            "AND u.createdDate BETWEEN :start AND :end")
    long countMaleUsers(@Param("start") LocalDate start, @Param("end")  LocalDate end);
    @Query("SELECT COUNT(u) FROM User u " +
            "WHERE u.genre = 'FEMALE' " +
            "AND u.createdDate BETWEEN :start AND :end")
    long countFemaleUsers(@Param("start") LocalDate start, @Param("end")  LocalDate end);

    @Query("SELECT DISTINCT u FROM User u " +
            "WHERE u.createdDate BETWEEN :start AND :end " +
            "ORDER BY u.lastName ASC, u.firstName ASC")
    List<User> findAllBetweenDates(@Param("start") LocalDate start, @Param("end")  LocalDate end);
    //

    // queries para KPI Fees
    @Query("SELECT COUNT(DISTINCT u.id) " +
            "FROM User u " +
            "JOIN u.fees f " +
            "WHERE f.feeType = 'SOCIAL' " +
            "AND f.dueDate < CURRENT_DATE " +
            "AND f.paymentDate IS NULL " +
            "AND f.period BETWEEN :start AND :end")
    Long countSocialDebtors(@Param("start") YearMonth start, @Param("end") YearMonth end);
    @Query("SELECT COUNT(DISTINCT u.id) " +
            "FROM User u " +
            "JOIN u.fees f " +
            "WHERE f.feeType = 'SOCIAL' " +
            "AND (f.dueDate >= CURRENT_DATE OR f.paymentDate IS NOT NULL) " +
            "AND f.period BETWEEN :start AND :end")
    Long countSocialUpToDateUsers(@Param("start") YearMonth start, @Param("end") YearMonth end);

    @Query("SELECT COUNT(DISTINCT u.id) " +
            "FROM User u " +
            "JOIN u.fees f " +
            "WHERE f.disciplineId = :disciplineId " +
            "AND f.dueDate < CURRENT_DATE " +
            "AND f.paymentDate IS NULL " +
            "AND f.period BETWEEN :start AND :end")
    Long countDisciplineDebtors(@Param("disciplineId") UUID disciplineId, @Param("start") YearMonth start, @Param("end") YearMonth end);
    @Query("SELECT COUNT(DISTINCT u.id) " +
            "FROM User u " +
            "JOIN u.fees f " +
            "WHERE f.disciplineId = :disciplineId " +
            "AND (f.dueDate >= CURRENT_DATE OR f.paymentDate IS NOT NULL) " +
            "AND f.period BETWEEN :start AND :end")
    Long countDisciplineUpToDateUsers(@Param("disciplineId") UUID disciplineId, @Param("start") YearMonth start, @Param("end") YearMonth end);
    //




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
