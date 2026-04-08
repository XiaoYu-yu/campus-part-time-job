package com.cangqiong.takeaway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CampusStep05IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldQueryAfterSaleResultPageAndRecordRefundExecution() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String refundOrderId = createAfterSaleResolvedOrder(adminToken, customerToken, courierToken, "STEP05-AS-001");

        recordAfterSaleDecision(adminToken, refundOrderId, """
                {
                  "decisionType": "refund",
                  "decisionAmount": 3.25,
                  "decisionRemark": "记录退款决策"
                }
                """);

        assertEquals("PENDING", jdbcTemplate.queryForObject(
                "SELECT after_sale_execution_status FROM campus_relay_order WHERE id = ?",
                String.class,
                refundOrderId
        ));

        mockMvc.perform(get("/api/campus/admin/orders/after-sale")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("relayOrderId", refundOrderId)
                        .param("afterSaleDecisionType", "refund")
                        .param("afterSaleExecutionStatus", "pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].relayOrderId").value(refundOrderId))
                .andExpect(jsonPath("$.data.records[0].afterSaleExecutionStatus").value("PENDING"));

        mockMvc.perform(get("/api/campus/admin/orders/after-sale")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("afterSaleDecisionType", "INVALID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(get("/api/campus/admin/orders/after-sale")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("afterSaleExecutionStatus", "UNKNOWN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(get("/api/campus/admin/orders/{id}/after-sale-result", refundOrderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.relayOrderId").value(refundOrderId))
                .andExpect(jsonPath("$.data.afterSaleDecisionType").value("REFUND"))
                .andExpect(jsonPath("$.data.afterSaleExecutionStatus").value("PENDING"));

        String plainOrderId = createCampusOrder(customerToken, "STEP05-AS-PLAIN", "LIBRARY", "图书馆", "图书馆门口");
        mockPayCampusOrder(customerToken, plainOrderId);
        acceptCampusOrder(courierToken, plainOrderId);

        mockMvc.perform(get("/api/campus/admin/orders/{id}/after-sale-result", plainOrderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-execution", refundOrderId)
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "executionStatus": "SUCCESS",
                                  "executionRemark": "非管理员不得操作",
                                  "executionReferenceNo": "AS-EXEC-001"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-execution", refundOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "executionStatus": "SUCCESS",
                                  "executionRemark": "记录退款执行结果",
                                  "executionReferenceNo": "   "
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-execution", refundOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "executionStatus": "SUCCESS",
                                  "executionRemark": "记录退款执行结果",
                                  "executionReferenceNo": "  AS-EXEC-001  "
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("SUCCESS", jdbcTemplate.queryForObject(
                "SELECT after_sale_execution_status FROM campus_relay_order WHERE id = ?",
                String.class,
                refundOrderId
        ));
        assertEquals("AS-EXEC-001", jdbcTemplate.queryForObject(
                "SELECT after_sale_execution_reference_no FROM campus_relay_order WHERE id = ?",
                String.class,
                refundOrderId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT after_sale_executed_at FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                refundOrderId
        ));

        MvcResult timeline = mockMvc.perform(get("/api/campus/admin/orders/{id}/timeline", refundOrderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        assertTrue(extractNodeCodes(timeline).contains("AFTER_SALE_EXECUTION_RECORDED"));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-execution", refundOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "executionStatus": "FAILED",
                                  "executionRemark": "终态后重复写入应拒绝"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void shouldSupportCompensateFailureCorrectionAndRejectNoneExecution() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String compensateOrderId = createAfterSaleResolvedOrder(adminToken, customerToken, courierToken, "STEP05-AS-002");
        String noneOrderId = createAfterSaleResolvedOrder(adminToken, customerToken, courierToken, "STEP05-AS-003");

        recordAfterSaleDecision(adminToken, compensateOrderId, """
                {
                  "decisionType": "COMPENSATE",
                  "decisionAmount": 2.50,
                  "decisionRemark": "记录补偿决策"
                }
                """);

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-execution", compensateOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "executionStatus": "FAILED",
                                  "executionRemark": "首次执行失败"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("FAILED", jdbcTemplate.queryForObject(
                "SELECT after_sale_execution_status FROM campus_relay_order WHERE id = ?",
                String.class,
                compensateOrderId
        ));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-execution", compensateOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "executionStatus": "SUCCESS",
                                  "executionRemark": "人工纠正为成功"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("SUCCESS", jdbcTemplate.queryForObject(
                "SELECT after_sale_execution_status FROM campus_relay_order WHERE id = ?",
                String.class,
                compensateOrderId
        ));
        assertNull(jdbcTemplate.queryForObject(
                "SELECT after_sale_execution_reference_no FROM campus_relay_order WHERE id = ?",
                String.class,
                compensateOrderId
        ));

        recordAfterSaleDecision(adminToken, noneOrderId, """
                {
                  "decisionType": "NONE",
                  "decisionRemark": "无需进一步执行"
                }
                """);

        assertEquals("NOT_REQUIRED", jdbcTemplate.queryForObject(
                "SELECT after_sale_execution_status FROM campus_relay_order WHERE id = ?",
                String.class,
                noneOrderId
        ));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-execution", noneOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "executionStatus": "SUCCESS",
                                  "executionRemark": "NONE 不应允许记录执行结果"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void shouldRecordSinglePayoutAndRejectDuplicatePaidWrites() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String orderId = createCompletedOrder(customerToken, courierToken, "STEP05-ST-001");
        Long settlementId = findSettlementIdByOrderId(orderId);

        confirmSettlement(adminToken, settlementId, "确认结算");

        assertEquals("UNPAID", jdbcTemplate.queryForObject(
                "SELECT payout_status FROM campus_settlement_record WHERE id = ?",
                String.class,
                settlementId
        ));

        mockMvc.perform(post("/api/campus/admin/settlements/{id}/payout-result", settlementId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "payoutStatus": "PAID",
                                  "payoutRemark": "记录打款成功",
                                  "payoutReferenceNo": "   "
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(post("/api/campus/admin/settlements/{id}/payout-result", settlementId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "payoutStatus": "PAID",
                                  "payoutRemark": "记录打款成功",
                                  "payoutReferenceNo": "  PAY-001  "
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/campus/admin/settlements/{id}", settlementId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.payoutStatus").value("PAID"))
                .andExpect(jsonPath("$.data.payoutReferenceNo").value("PAY-001"));

        mockMvc.perform(post("/api/campus/admin/settlements/{id}/payout-result", settlementId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "payoutStatus": "FAILED",
                                  "payoutRemark": "已成功后不允许回写失败"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void shouldBatchRecordPayoutResultsAndQueryReconcileSummary() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");

        String orderA = createCompletedOrder(customerToken, courierToken, "STEP05-ST-002");
        String orderB = createCompletedOrder(customerToken, courierToken, "STEP05-ST-003");
        String orderC = createCompletedOrder(customerToken, courierToken, "STEP05-ST-004");

        Long settlementA = findSettlementIdByOrderId(orderA);
        Long settlementB = findSettlementIdByOrderId(orderB);
        Long settlementC = findSettlementIdByOrderId(orderC);

        confirmSettlement(adminToken, settlementA, "确认结算-A");
        confirmSettlement(adminToken, settlementB, "确认结算-B");

        mockMvc.perform(post("/api/campus/admin/settlements/{id}/payout-result", settlementB)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "payoutStatus": "FAILED",
                                  "payoutRemark": "首次打款失败"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/campus/admin/settlements/batch-payout-result")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "settlementIds": [%d, %d, %d, %d, 999999],
                                  "payoutStatus": "PAID",
                                  "payoutRemark": "批量打款登记",
                                  "batchNo": "BATCH-20260408"
                                }
                                """.formatted(settlementA, settlementA, settlementB, settlementC)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalRequested").value(4))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.skippedCount").value(2))
                .andExpect(jsonPath("$.data.failedCount").value(0));

        assertEquals("PAID", jdbcTemplate.queryForObject(
                "SELECT payout_status FROM campus_settlement_record WHERE id = ?",
                String.class,
                settlementA
        ));
        assertEquals("PAID", jdbcTemplate.queryForObject(
                "SELECT payout_status FROM campus_settlement_record WHERE id = ?",
                String.class,
                settlementB
        ));
        assertEquals("BATCH-20260408", jdbcTemplate.queryForObject(
                "SELECT payout_reference_no FROM campus_settlement_record WHERE id = ?",
                String.class,
                settlementB
        ));

        mockMvc.perform(get("/api/campus/admin/settlements")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("payoutStatus", "PAID")
                        .param("relayOrderId", orderA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].relayOrderId").value(orderA))
                .andExpect(jsonPath("$.data.records[0].payoutStatus").value("PAID"));

        MvcResult summaryResult = mockMvc.perform(get("/api/campus/admin/settlements/reconcile-summary")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("settlementStatus", "SETTLED")
                        .param("courierProfileId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        JsonNode summary = extractDataNode(summaryResult);
        Long expectedPaidCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM campus_settlement_record WHERE settlement_status = 'SETTLED' AND courier_profile_id = 2 AND payout_status = 'PAID'",
                Long.class
        );
        BigDecimal expectedPaidAmount = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(pending_amount), 0) FROM campus_settlement_record WHERE settlement_status = 'SETTLED' AND courier_profile_id = 2 AND payout_status = 'PAID'",
                BigDecimal.class
        );
        assertEquals(expectedPaidCount.longValue(), summary.path("paidCount").asLong());
        assertEquals(expectedPaidAmount.setScale(2), new BigDecimal(summary.path("totalPaidAmount").asText()).setScale(2));
        assertEquals(new BigDecimal("0.00"), new BigDecimal(summary.path("totalPendingAmount").asText()).setScale(2));
        assertEquals(new BigDecimal("0.00"), new BigDecimal(summary.path("totalFailedAmount").asText()).setScale(2));
    }

    @Test
    void shouldQueryOrderLevelLocationReportsAndExceptionSummary() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");

        String orderId = createCampusOrder(customerToken, "STEP05-OPS-001", "LIBRARY", "图书馆", "图书馆南门");
        mockPayCampusOrder(customerToken, orderId);
        acceptCampusOrder(courierToken, orderId);

        mockMvc.perform(post("/api/campus/courier/orders/{id}/exception-report", orderId)
                        .header("Authorization", "Bearer " + courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "exceptionType": "TRAFFIC_DELAY",
                                  "exceptionRemark": "校内道路临时封控"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        reportLocation(courierToken, orderId, "AUTO", "point-a", "29.5630100", "106.5515500");
        reportLocation(courierToken, orderId, "MANUAL", "point-b", "29.5630200", "106.5515600");

        mockMvc.perform(get("/api/campus/admin/orders/{id}/location-reports", orderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].relayOrderId").value(orderId))
                .andExpect(jsonPath("$.data.records[0].note").value("point-b"));

        mockMvc.perform(get("/api/campus/admin/orders/{id}/location-reports", "NOT-FOUND")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));

        mockMvc.perform(get("/api/campus/admin/orders/{id}/exception-summary", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.relayOrderId").value(orderId))
                .andExpect(jsonPath("$.data.latestExceptionType").value("TRAFFIC_DELAY"))
                .andExpect(jsonPath("$.data.locationReportCount").value(2))
                .andExpect(jsonPath("$.data.latestLocationReportedAt").isNotEmpty());

        String orderWithoutOps = createCampusOrder(customerToken, "STEP05-OPS-002", "LIBRARY", "图书馆", "图书馆北门");
        mockPayCampusOrder(customerToken, orderWithoutOps);

        mockMvc.perform(get("/api/campus/admin/orders/{id}/exception-summary", orderWithoutOps)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.relayOrderId").value(orderWithoutOps))
                .andExpect(jsonPath("$.data.latestExceptionType").isEmpty())
                .andExpect(jsonPath("$.data.locationReportCount").value(0));
    }

    private String createAfterSaleResolvedOrder(
            String adminToken,
            String customerToken,
            String courierToken,
            String externalOrderRef
    ) throws Exception {
        String orderId = createCompletedOrder(customerToken, courierToken, externalOrderRef);
        openAfterSale(customerToken, orderId, "需要平台介入处理");
        handleAfterSale(adminToken, orderId, "RESOLVED");
        return orderId;
    }

    private String createCompletedOrder(String customerToken, String courierToken, String externalOrderRef) throws Exception {
        String orderId = createCampusOrder(customerToken, externalOrderRef, "LIBRARY", "图书馆", "图书馆一楼门口");
        mockPayCampusOrder(customerToken, orderId);
        acceptCampusOrder(courierToken, orderId);
        pickupCampusOrder(courierToken, orderId, "/api/files/" + externalOrderRef + "-proof.jpg");
        advanceDeliverTwice(courierToken, orderId);
        confirmCampusOrder(customerToken, orderId);
        return orderId;
    }

    private void recordAfterSaleDecision(String adminToken, String orderId, String json) throws Exception {
        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-decision", orderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void confirmSettlement(String adminToken, Long settlementId, String remark) throws Exception {
        mockMvc.perform(post("/api/campus/admin/settlements/{id}/confirm", settlementId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "settleRemark": "%s"
                                }
                                """.formatted(remark)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private Long findSettlementIdByOrderId(String orderId) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM campus_settlement_record WHERE relay_order_id = ?",
                Long.class,
                orderId
        );
    }

    private void handleAfterSale(String adminToken, String orderId, String action) throws Exception {
        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-handle", orderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "action": "%s",
                                  "handleRemark": "管理员处理售后"
                                }
                                """.formatted(action)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void openAfterSale(String customerToken, String orderId, String reason) throws Exception {
        mockMvc.perform(post("/api/campus/customer/orders/{id}/after-sale", orderId)
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "afterSaleReason": "%s"
                                }
                                """.formatted(reason)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void reportLocation(String courierToken, String orderId, String source, String note, String latitude, String longitude) throws Exception {
        mockMvc.perform(post("/api/campus/courier/location-reports")
                        .header("Authorization", "Bearer " + courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "relayOrderId": "%s",
                                  "latitude": %s,
                                  "longitude": %s,
                                  "source": "%s",
                                  "note": "%s"
                                }
                                """.formatted(orderId, latitude, longitude, source, note)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void mockPayCampusOrder(String customerToken, String orderId) throws Exception {
        mockMvc.perform(post("/api/campus/customer/orders/{id}/mock-pay", orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void acceptCampusOrder(String courierToken, String orderId) throws Exception {
        mockMvc.perform(post("/api/campus/courier/orders/{id}/accept", orderId)
                        .header("Authorization", "Bearer " + courierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void pickupCampusOrder(String courierToken, String orderId, String pickupProofPath) throws Exception {
        mockMvc.perform(post("/api/campus/courier/orders/{id}/pickup", orderId)
                        .header("Authorization", "Bearer " + courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pickupProofImageUrl": "%s",
                                  "courierRemark": "已完成取餐"
                                }
                                """.formatted(pickupProofPath)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void advanceDeliverTwice(String courierToken, String orderId) throws Exception {
        mockMvc.perform(post("/api/campus/courier/orders/{id}/deliver", orderId)
                        .header("Authorization", "Bearer " + courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courierRemark": "正在配送中"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/campus/courier/orders/{id}/deliver", orderId)
                        .header("Authorization", "Bearer " + courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courierRemark": "已送达"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void confirmCampusOrder(String customerToken, String orderId) throws Exception {
        mockMvc.perform(post("/api/campus/customer/orders/{id}/confirm", orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private String createCampusOrder(
            String customerToken,
            String externalOrderRef,
            String targetType,
            String deliveryBuilding,
            String deliveryDetail
    ) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/campus/customer/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pickupPointId": 1,
                                  "targetType": "%s",
                                  "deliveryBuilding": "%s",
                                  "deliveryDetail": "%s",
                                  "contactName": "张三",
                                  "contactPhone": "13900139000",
                                  "foodDescription": "Step05 联调订单",
                                  "externalPlatformName": "美团",
                                  "externalOrderRef": "%s",
                                  "pickupCode": "S05",
                                  "remark": "Step05 测试订单",
                                  "tipFee": 2.00,
                                  "urgentFlag": 0
                                }
                                """.formatted(targetType, deliveryBuilding, deliveryDetail, externalOrderRef)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        return extractDataText(result);
    }

    private String loginAdmin() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/employees/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"phone":"13800138000","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        return extractToken(result);
    }

    private String loginUser(String phone) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"phone":"%s","password":"123456"}
                                """.formatted(phone)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        return extractToken(result);
    }

    private String loginCourier(String phone) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/campus/courier/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"phone":"%s","password":"123456"}
                                """.formatted(phone)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        return extractDataNode(result).path("token").asText();
    }

    private List<String> extractNodeCodes(MvcResult result) throws Exception {
        JsonNode items = extractDataNode(result).path("items");
        return objectMapper.convertValue(
                items.findValuesAsText("nodeCode"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );
    }

    private JsonNode extractDataNode(MvcResult result) throws Exception {
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        return response.path("data");
    }

    private String extractToken(MvcResult result) throws Exception {
        return extractDataNode(result).path("token").asText();
    }

    private String extractDataText(MvcResult result) throws Exception {
        return extractDataNode(result).asText();
    }
}
