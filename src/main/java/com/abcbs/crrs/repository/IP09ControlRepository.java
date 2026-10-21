package com.abcbs.crrs.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.abcbs.crrs.entity.ControlPK;
import com.abcbs.crrs.entity.P09Control;

public interface IP09ControlRepository extends JpaRepository<P09Control, ControlPK> {

    public interface P09ControlProjection {
        String getCntrlRefundType();
        LocalDate getCntrlToDate();
        String getCntrlRefundNarr();
    }

    @Query("""
        SELECT c.controlId.cntrlRefundType AS cntrlRefundType,
               c.cntrlToDate AS cntrlToDate,
               c.cntrlRefundNarr AS cntrlRefundNarr
        FROM P09Control c
        WHERE c.controlId.cntrlRefundType = :refundType
          AND c.controlId.cntrlOpenInd = 'O'
        ORDER BY c.controlId.cntrlRefundType
    """)
    List<P09ControlProjection> fetchControlRows(String refundType);
}