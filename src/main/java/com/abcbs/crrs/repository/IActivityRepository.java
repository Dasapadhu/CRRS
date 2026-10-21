package com.abcbs.crrs.repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.abcbs.crrs.entity.ActivityPK;
import com.abcbs.crrs.entity.P09Activity;

public interface IActivityRepository extends JpaRepository<P09Activity, ActivityPK> {

    // ================================================================
    // ACC Query — JPQL (entity paths allowed)
    // ================================================================
    @Query("""
           SELECT a FROM P09Activity a
           WHERE a.aId.crRefundType = :refundType
             AND a.aId.crCntrlDate = :cntrlDate
             AND a.aId.crCntrlNbr = :cntrlNbr
             AND a.aId.actActivity = 'ACC'
             AND a.aId.actActivityDate = :activityDate
           ORDER BY a.aId.crRefundType,
                    a.actXrefDate,
                    a.actXrefNbr,
                    a.aId.crCntrlDate,
                    a.aId.crCntrlNbr
           """)
    List<P09Activity> findActivitiesForAcc(
            @Param("refundType")   String refundType,
            @Param("cntrlDate")    LocalDate cntrlDate,
            @Param("cntrlNbr")     String cntrlNbr,
            @Param("activityDate") LocalDate activityDate
    );

    // ================================================================
    // PX03 SELECT — JPQL
    // ================================================================
    @Query("""
           SELECT a FROM P09Activity a
           WHERE a.aId.crRefundType = :refundType
             AND a.aId.crCntrlDate = :cntrlDate
             AND a.aId.crCntrlNbr = :cntrlNbr
             AND (a.aId.actActivity = 'PRR' OR a.aId.actActivity = 'FRR')
             AND a.actProcessedInd = ' '
           ORDER BY a.aId.crRefundType,
                    a.aId.crCntrlDate,
                    a.aId.crCntrlNbr,
                    a.actWorkingBal,
                    a.aId.actActivityDate,
                    a.aId.actActivity
           """)
    List<P09Activity> fetchPendingActivities(
            @Param("refundType") String refundType,
            @Param("cntrlDate")  LocalDate cntrlDate,
            @Param("cntrlNbr")   String cntrlNbr
    );

    // ================================================================
    // PX03 UPDATE — JPQL
    // ================================================================
    @Modifying
    @Transactional
    @Query("""
           UPDATE P09Activity a
           SET a.actProcessedInd = :processedInd
           WHERE a.aId.crRefundType = :refundType
             AND a.aId.crCntrlDate = :cntrlDate
             AND a.aId.crCntrlNbr = :cntrlNbr
             AND a.aId.actActivityDate = :activityDate
             AND a.aId.actActivity = :activity
             AND a.aId.actTimestamp = :actTimestamp
           """)
    int updateProcessedInd(
            @Param("processedInd") String processedInd,
            @Param("refundType")   String refundType,
            @Param("cntrlDate")    LocalDate cntrlDate,
            @Param("cntrlNbr")     String cntrlNbr,
            @Param("activityDate") LocalDate activityDate,
            @Param("activity")     String activity,
            @Param("actTimestamp") LocalDateTime actTimestamp
    );
}