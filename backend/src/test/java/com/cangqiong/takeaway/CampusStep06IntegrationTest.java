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
class CampusStep06IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldAllowCustomerQueryOwnAfterSaleResultAndRejectInvalidAccess() throws Exception {
        String adminToken = loginAdmin();
        String ownerToken = loginUser("13900139000");
        String otherCustomerToken = loginUser("13900139001");
        String courierToken = loginCourier("13900139001");

        String orderId = createAfterSaleResolvedOrder(adminToken, ownerToken, courierToken, "STEP06-ASR-001");
        recordAfterSaleDecision(adminToken, orderId, """
                {
                  "decisionType": "refund",
                  "decisionAmount": 4.20,
                  "decisionRemark": "记录退款决策"
                }
                """);
        recordAfterSaleExecution(adminToken, orderId, """
                {
                  "executionStatus": "SUCCESS",
                  "executionRemark": "退款执行成功",
                  "executionReferenceNo": " ASR-REF-001 "
                }
                """);

        mockMvc.perform(get("/api/campus/customer/orders/{id}/after-sale-result", orderId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.relayOrderId").value(orderId))
                .andExpect(jsonPath("$.data.orderStatus").value("AFTER_SALE_RESOLVED"))
                .andExpect(jsonPath("$.data.afterSaleExecutionStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.data.customerReceiptStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.data.decisionAmount").value(4.20))
                .andExpect(jsonPath("$.data.lastUpdatedAt").isNotEmpty())
                .andExpect(jsonPath("$.data.afterSaleExecutedByEmployeeId").doesNotExist())
                .andExpect(jsonPath("$.data.afterSaleExecutionReferenceNo").doesNotExist());

        mockMvc.perform(get("/api/campus/customer/orders/{id}/after-sale-result", orderId)
                        .header("Authorization", "Bearer " + otherCustomerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));

        String plainOrderId = createCompletedOrder(ownerToken, courierToken, "STEP06-ASR-PLAIN");
        mockMvc.perform(get("/api/campus/customer/orders/{id}/after-sale-result", plainOrderId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void shouldExposeAfterSaleExecutionPageAndCorrectionFlags() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");

        String orderId = createAfterSaleResolvedOrder(adminToken, customerToken, courierToken, "STEP06-EXEC-001");
        recordAfterSaleDecision(adminToken, orderId, """
                {
                  "decisionType": "COMPENSATE",
                  "decisionAmount": 2.50,
                  "decisionRemark": "记录补偿决策"
                }
                """);
        recordAfterSaleExecution(adminToken, orderId, """
                {
                  "executionStatus": "FAILED",
                  "executionRemark": "首次补偿失败"
                }
                """);
        recordAfterSaleExecution(adminToken, orderId, """
                {
                  "executionStatus": "SUCCESS",
                  "executionRemark": "人工纠正为成功"
                }
                """);

        assertEquals(Integer.valueOf(1), jdbcTemplate.queryForObject(
                "SELECT after_sale_execution_corrected FROM campus_relay_order WHERE id = ?",
                Integer.class,
                orderId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT after_sale_execution_corrected_at FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                orderId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT after_sale_execution_corrected_by_employee_id FROM campus_relay_order WHERE id = ?",
                Long.class,
                orderId
        ));

        mockMvc.perform(get("/api/campus/admin/orders/after-sale-executions")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("correctedOnly", "true")
                        .param("relayOrderId", orderId)
                        .param("decisionType", "compensate")
                        .param("afterSaleExecutionStatus", "success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].relayOrderId").value(orderId))
                .andExpect(jsonPath("$.data.records[0].afterSaleExecutionCorrected").value(1))
                .andExpect(jsonPath("$.data.records[0].afterSaleExecutionCorrectedAt").isNotEmpty());

        mockMvc.perform(get("/api/campus/admin/orders/{id}/after-sale-result", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.afterSaleExecutionCorrected").value(1))
                .andExpect(jsonPath("$.data.afterSaleExecutionCorrectedByEmployeeId").isNotEmpty())
                .andExpect(jsonPath("$.data.afterSaleExecutionCorrectedAt").isNotEmpty());
    }

    @Test
    void shouldSupportSettlementBatchAuditAndVerifyFlow() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");

        String orderId1 = createCompletedOrder(customerToken, courierToken, "STEP06-ST-001");
        String orderId2 = createCompletedOrder(customerToken, courierToken, "STEP06-ST-002");
        String orderId3 = createCompletedOrder(customerToken, courierToken, "STEP06-ST-003");

        Long settlementId1 = findSettlementIdByOrderId(orderId1);
        Long settlementId2 = findSettlementIdByOrderId(orderId2);
        Long settlementId3 = findSettlementIdByOrderId(orderId3);

        confirmSettlement(adminToken, settlementId1, "确认结算-1");
        confirmSettlement(adminToken, settlementId2, "确认结算-2");

        mockMvc.perform(post("/api/campus/admin/settlements/batch-payout-result")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "settlementIds": [%d, %d, %d, %d],
                                  "payoutStatus": "PAID",
                                  "payoutRemark": "批量打款成功"
                                }
                                """.formatted(settlementId1, settlementId2, settlementId2, settlementId3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalRequested").value(3))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.skippedCount").value(1))
                .andExpect(jsonPath("$.data.failedCount").value(0));

        String batchNo1 = jdbcTemplate.queryForObject(
                "SELECT payout_batch_no FROM campus_settlement_record WHERE id = ?",
                String.class,
                settlementId1
        );
        String batchNo2 = jdbcTemplate.queryForObject(
                "SELECT payout_batch_no FROM campus_settlement_record WHERE id = ?",
                String.class,
                settlementId2
        );
        String batchNo3 = jdbcTemplate.queryForObject(
                "SELECT payout_batch_no FROM campus_settlement_record WHERE id = ?",
                String.class,
                settlementId3
        );
        assertNotNull(batchNo1);
        assertEquals(batchNo1, batchNo2);
        assertNull(batchNo3);

        mockMvc.perform(get("/api/campus/admin/settlements")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("payoutBatchNo", batchNo1)
                        .param("payoutVerified", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.records[0].payoutBatchNo").value(batchNo1))
                .andExpect(jsonPath("$.data.records[0].payoutVerified").value(0));

        MvcResult payoutBatchPage = mockMvc.perform(get("/api/campus/admin/settlements/payout-batches")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("payoutStatus", "PAID")
                        .param("payoutVerified", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        JsonNode payoutBatchRecords = extractDataNode(payoutBatchPage).path("records");
        JsonNode matchedBatch = null;
        for (JsonNode payoutBatchRecord : payoutBatchRecords) {
            if (batchNo1.equals(payoutBatchRecord.path("payoutBatchNo").asText())) {
                matchedBatch = payoutBatchRecord;
                break;
            }
        }
        assertNotNull(matchedBatch);
        assertEquals(2L, matchedBatch.path("totalCount").asLong());

        mockMvc.perform(get("/api/campus/admin/settlements/payout-batches/{batchNo}", batchNo1)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.payoutBatchNo").value(batchNo1))
                .andExpect(jsonPath("$.data.records.length()").value(2))
                .andExpect(jsonPath("$.data.records[0].payoutVerified").value(0));

        mockMvc.perform(post("/api/campus/admin/settlements/{id}/verify-payout", settlementId1)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "verifyRemark": "人工核对到账"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/campus/admin/settlements/{id}", settlementId1)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.payoutVerified").value(1))
                .andExpect(jsonPath("$.data.payoutVerifiedAt").isNotEmpty())
                .andExpect(jsonPath("$.data.payoutVerifyRemark").value("人工核对到账"));

        MvcResult payoutBatchDetail = mockMvc.perform(get("/api/campus/admin/settlements/payout-batches/{batchNo}", batchNo1)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        JsonNode detailRecords = extractDataNode(payoutBatchDetail).path("records");
        JsonNode verifiedRecord = null;
        for (JsonNode detailRecord : detailRecords) {
            if (settlementId1.equals(detailRecord.path("id").asLong())) {
                verifiedRecord = detailRecord;
                break;
            }
        }
        assertNotNull(verifiedRecord);
        assertEquals(1, verifiedRecord.path("payoutVerified").asInt());
        assertEquals("人工核对到账", verifiedRecord.path("payoutVerifyRemark").asText());

        mockMvc.perform(post("/api/campus/admin/settlements/{id}/verify-payout", settlementId1)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "verifyRemark": "重复核对应拒绝"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(post("/api/campus/admin/settlements/{id}/verify-payout", settlementId3)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "verifyRemark": "非打款成功记录不得核对"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
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

    private void recordAfterSaleExecution(String adminToken, String orderId, String json) throws Exception {
        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-execution", orderId)
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
                                  "foodDescription": "Step06 联调订单",
                                  "externalPlatformName": "美团",
                                  "externalOrderRef": "%s",
                                  "pickupCode": "S06",
                                  "remark": "Step06 测试订单",
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
