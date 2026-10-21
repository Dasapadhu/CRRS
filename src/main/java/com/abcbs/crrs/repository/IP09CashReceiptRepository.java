package com.abcbs.crrs.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.abcbs.crrs.entity.CashReceiptPK;
import com.abcbs.crrs.entity.P09CashReceipt;

public interface IP09CashReceiptRepository extends JpaRepository<P09CashReceipt, CashReceiptPK> {	
    // PX02 - fetch pending cash receipts (COBOL WHERE conditions)
    @Query("""
        SELECT c
        FROM P09CashReceipt c
        WHERE c.crId.crRefundType = :refundType
          AND c.crStatusDate <= :statusDate
          AND c.crStatusText = 'PENDED'
          AND (c.crPendFinAct = 'PRR' OR c.crPendFinAct = 'FRR')
          AND (c.crClaimType = 'FEP ' OR c.crClaimType = 'FEPP')
          AND c.crCorp = :corp
        ORDER BY c.crId.crRefundType, c.crId.crCntrlDate, c.crId.crCntrlNbr
    """)
    List<P09CashReceipt> findPendingCashReceipts(String refundType, LocalDate statusDate, String corp);

    // DB-MODIFY V_P09_CASH_RECEIPT (COBOL UPDATE block)
    @Modifying
    @Query("""
        UPDATE P09CashReceipt c
        SET c.crReceiptBal = :receiptBal,
            c.crStatusText = :statusText,
            c.crStatusDate = :statusDate,
            c.crPendFinAct = :pendFinAct,
            c.crRemDailyInd = :remDailyInd,
            c.crRemIdType = :remIdType,
            c.crRemIdNbr = :remIdNbr,
            c.crRemNationalId = :remNationalId,
            c.crRemTaxIdNbr = :remTaxIdNbr,
            c.crRemAddressee = :remAddressee,
            c.crRemAddress1 = :remAddress1,
            c.crRemAddress2 = :remAddress2,
            c.crRemCity = :remCity,
            c.crRemState = :remState,
            c.crRemZip5 = :remZip5,
            c.crRemZip4 = :remZip4
        WHERE c.crId.crRefundType = :refundType
          AND c.crId.crCntrlDate = :cntrlDate
          AND c.crId.crCntrlNbr = :cntrlNbr
    """)
    int updateCashReceiptRecord(
            BigDecimal receiptBal,
            String statusText,
            LocalDate statusDate,
            String pendFinAct,
            String remDailyInd,
            String remIdType,
            String remIdNbr,
            String remNationalId,
            String remTaxIdNbr,
            String remAddressee,
            String remAddress1,
            String remAddress2,
            String remCity,
            String remState,
            String remZip5,
            String remZip4,
            String refundType,
            LocalDate cntrlDate,
            String cntrlNbr
    );
}