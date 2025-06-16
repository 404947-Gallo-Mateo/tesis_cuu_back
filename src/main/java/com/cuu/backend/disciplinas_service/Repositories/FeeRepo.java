package com.cuu.backend.disciplinas_service.Repositories;

import com.cuu.backend.disciplinas_service.Models.Entities.Fee;
import com.cuu.backend.disciplinas_service.Models.Entities.User;
import com.cuu.backend.disciplinas_service.Models.Enums.FeeType;
import com.cuu.backend.disciplinas_service.Models.KPIs.KpiRevenuePerPeriodDistribution;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeeRepo extends JpaRepository<Fee, UUID> {

    @Query("SELECT COALESCE(SUM(f.amount), 0) " +
            "FROM Fee f " +
            "WHERE f.paid = true " +
            "AND f.feeType = :feeType")
    BigDecimal sumPaidAmountsByFeeType(@Param("feeType") FeeType feeType);

    @Query("SELECT COALESCE(SUM(f.amount), 0) " +
            "FROM Fee f " +
            "WHERE f.paid = true " +
            "AND f.feeType = :feeType " +
            "AND f.disciplineId = :disciplineId ")
    BigDecimal sumPaidAmountsByFeeTypeAndDisciplineId(@Param("feeType") FeeType feeType, @Param("disciplineId") UUID disciplineId);

    @Query("SELECT f.paymentDate as paymentDate, SUM(f.amount) as revenue " +
            "FROM Fee f " +
            "WHERE f.paid = true " +
            "AND (:feeType IS NULL OR f.feeType = :feeType) " +
            "GROUP BY f.paymentDate " +
            "ORDER BY f.paymentDate")
    List<Object[]> findRevenueDistributionByPeriodFiltered(
            @Param("feeType") FeeType feeType);

    @Query("SELECT f.paymentDate as paymentDate, SUM(f.amount) as revenue " +
            "FROM Fee f " +
            "WHERE f.paid = true " +
            "AND (:feeType IS NULL OR f.feeType = :feeType) " +
            "AND (:disciplineId IS NULL OR f.disciplineId = :disciplineId) " +
            "GROUP BY f.paymentDate " +
            "ORDER BY f.paymentDate")
    List<Object[]> findRevenueDistributionByPeriodAndDisciplineIdFiltered(
            @Param("feeType") FeeType feeType,
            @Param("disciplineId") UUID disciplineId);

    List<Fee> findByFeeType(FeeType feeType);
    List<Fee> findByUser(User user);
    List<Fee> findByUserKeycloakId(String userKeycloakId);
    List<Fee> findByPayerEmail(String payerEmail);
    List<Fee> findByDisciplineId(UUID disciplineId);
    @Query("SELECT f FROM Fee f WHERE f.disciplineId IS NULL")
    List<Fee> findTypeSocialFees();
    List<Fee> findByFeeTypeAndUserKeycloakId(FeeType feeType, String userKeycloakId);
    Optional<Fee> findByUserKeycloakIdAndDisciplineIdAndPeriod(String userKeycloakId, UUID disciplineId, YearMonth period);
    Optional<Fee> findByUserKeycloakIdAndFeeTypeAndDisciplineIdAndPeriod(String userKeycloakId, FeeType feeType, UUID disciplineId, YearMonth period);

    //para buscar una Fee SOCIAL especifica
    Optional<Fee> findByUserKeycloakIdAndFeeTypeAndPeriod(String userKeycloakId, FeeType feeType, YearMonth period);

    List<Fee> findByUserKeycloakIdAndDisciplineId(String userKeycloakId, UUID disciplineId);

    //encuentra la fee (del FeeType indicado, SOCIAL o DISCIPLINE) mas reciente de un Alumno especifico
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Fee f " +
            "WHERE f.feeType = :feeType " +
            "AND f.userKeycloakId = :userKeycloakId " +
            "ORDER BY f.period DESC LIMIT 1")
    Optional<Fee> findLatestByFeeTypeAndStudentKeycloakId(
            @Param("feeType") FeeType feeType,
            @Param("userKeycloakId") String userKeycloakId
    );

    //enceuntra la fee (del FeeType y Discipline indicado) mas reciente de un Alumno especifico
    @Query("SELECT f FROM Fee f " +
            "WHERE f.feeType = :feeType " +
            "AND f.disciplineId = :disciplineId " +
            "AND f.userKeycloakId = :userKeycloakId " +
            "ORDER BY f.period DESC LIMIT 1")
    Optional<Fee> findLatestByFeeTypeAndDisciplineIdAndStudentKeycloakId(
            @Param("feeType") FeeType feeType,
            @Param("disciplineId") UUID disciplineId,
            @Param("userKeycloakId") String userKeycloakId
    );
}
