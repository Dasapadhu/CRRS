package com.abcbs.crrs.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.abcbs.crrs.entity.P09Summary;
import com.abcbs.crrs.entity.SummaryPK;

public interface IP09SummaryRepository extends JpaRepository<P09Summary, SummaryPK> {

    // SELECT (COBOL)
    @Query("""
        SELECT s FROM P09Summary s
        WHERE s.sId.sumLocationID = :locationId
          AND s.sId.sumClerkId = :clerkId
    """)
    Optional<P09Summary> findSummary(String locationId, String clerkId);

    // UPDATE (COBOL)
    @Modifying
    @Query("""
        UPDATE P09Summary s
        SET s.sumDeletionsCnt = :sumDelCnt,
            s.sumDeletionsAmt = :sumDelAmt,
            s.sumEndingCnt    = :sumEndCnt,
            s.sumEndingAmt    = :sumEndAmt
        WHERE s.sId.sumLocationID = :locationId
          AND s.sId.sumClerkId = :clerkId
    """)
    int updateSummary(Integer sumDelCnt,
                      BigDecimal sumDelAmt,
                      Integer sumEndCnt,
                      BigDecimal sumEndAmt,
                      String locationId,
                      String clerkId);
}