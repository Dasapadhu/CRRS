package com.abcbs.crrs.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.abcbs.crrs.entity.BatchPK;
import com.abcbs.crrs.entity.P09Batch;
import com.abcbs.crrs.jobs.P09175.P09175BatchView;

/**
 * COBOL DECLARE BATCHRR CURSOR equivalent:
 *   WHERE (BT_BATCH_PREFIX||BT_BATCH_DATE||BT_BATCH_SUFFIX||CR_REFUND_TYPE) > :ck
 *     AND BT_POSTED_IND = ' '
 *   ORDER BY BT_BATCH_PREFIX, BT_BATCH_DATE, BT_BATCH_SUFFIX, CR_REFUND_TYPE
 *
 * If BT_BATCH_DATE is DATE in Oracle, change the WHERE to
 *   (BT_BATCH_PREFIX||TO_CHAR(BT_BATCH_DATE,'YYMMDD')||BT_BATCH_SUFFIX||CR_REFUND_TYPE) > :ck
 * and ensure your BatchPK.btBatchDate holds YYMMDD.
 */
public interface IP09BatchRepository extends JpaRepository<P09Batch, BatchPK> {

	@Query(value = """
			SELECT
			  CR_REFUND_TYPE,
			  BT_BATCH_PREFIX,
			  BT_BATCH_DATE,
			  BT_BATCH_SUFFIX,
			  BT_BATCH_CNT,
			  BT_BATCH_AMT,
			  BT_CONTROL_DATE,
			  BT_RECEIVED_DATE,
			  BT_DEPOSIT_DATE,
			  BT_ENTRY_DATE,
			  BT_STATUS_DATE,
			  BT_STATUS,
			  BT_POSTED_IND
			FROM P09_BATCH
			WHERE CONCAT(CONCAT(CONCAT(BT_BATCH_PREFIX, BT_BATCH_DATE), BT_BATCH_SUFFIX), CR_REFUND_TYPE) > :ck
			  AND BT_POSTED_IND = ' '
			ORDER BY BT_BATCH_PREFIX, BT_BATCH_DATE, BT_BATCH_SUFFIX, CR_REFUND_TYPE
			""", nativeQuery = true)
			List<P09Batch> fetchAfterKeyUnposted(@Param("ck") String ck, Pageable page);
    
    @Query("""
            SELECT
                b.bId.crRefundType   AS refundType,
                b.bId.btBatchPrefix  AS batchPrefix,
                b.bId.btBatchDate    AS batchDate,
                b.bId.btBatchSuffix  AS batchSuffix,
                b.btBatchCnt         AS batchCnt,
                b.btBatchAmt         AS batchAmt,
                b.btControlDate      AS controlDate,
                b.btReceivedDate     AS receivedDate,
                b.btDepositDate      AS depositDate,
                b.btEntryDate        AS entryDate,
                b.btStatusDate       AS statusDate,
                b.btStatus           AS status
            FROM P09Batch b
            WHERE b.btPostedInd = 'T'
              AND CONCAT(
                    CONCAT(
                        CONCAT(b.bId.crRefundType, b.bId.btBatchPrefix),
                        b.bId.btBatchDate
                    ),
                    b.bId.btBatchSuffix
                  ) > :checkpointKey
            ORDER BY
                b.bId.crRefundType,
                b.bId.btBatchPrefix,
                b.bId.btBatchDate,
                b.bId.btBatchSuffix
            """)
        List<P09175BatchView> fetchPx01Cursor(@Param("checkpointKey") String checkpointKey);

        @Transactional
        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("""
            UPDATE P09Batch b
               SET b.btPostedInd = :newPostedInd
             WHERE b.bId.btBatchSuffix = :batchSuffix
               AND b.bId.btBatchDate   = :batchDate
               AND b.bId.btBatchPrefix = :batchPrefix
               AND b.bId.crRefundType  = :refundType
               AND b.btPostedInd       = :currentPostedInd
            """)
        int updatePostedIndicator(@Param("newPostedInd") String newPostedInd,
                                  @Param("batchSuffix") String batchSuffix,
                                  @Param("batchDate") String batchDate,
                                  @Param("batchPrefix") String batchPrefix,
                                  @Param("refundType") String refundType,
                                  @Param("currentPostedInd") String currentPostedInd);
}