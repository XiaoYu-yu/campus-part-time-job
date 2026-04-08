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
class CampusStep04IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldQueryAfterSaleOrdersAndValidateFilters() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");

        String resolvedOrderId = createAfterSaleOrder(adminToken, customerToken, courierToken, "STEP04-AFTER-001", "RESOLVED");
        String rejectedOrderId = createAfterSaleOrder(adminToken, customerToken, courierToken, "STEP04-AFTER-002", "REJECTED");

        mockMvc.perform(get("/api/campus/admin/orders/after-sale")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("relayOrderId", resolvedOrderId)
                        .param("afterSaleHandleAction", "resolved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].relayOrderId").value(resolvedOrderId))
                .andExpect(jsonPath("$.data.records[0].orderStatus").value("AFTER_SALE_RESOLVED"));

        mockMvc.perform(get("/api/campus/admin/orders/after-sale")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].relayOrderId").value(rejectedOrderId))
                .andExpect(jsonPath("$.data.records[1].relayOrderId").value(resolvedOrderId));

        mockMvc.perform(get("/api/campus/admin/orders/after-sale")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("orderStatus", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(get("/api/campus/admin/orders/after-sale")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("afterSaleHandleAction", "INVALID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void shouldRecordRefundDecisionAndRejectInvalidDecisionAttempts() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String resolvedOrderId = createAfterSaleOrder(adminToken, customerToken, courierToken, "STEP04-DECISION-001", "RESOLVED");
        String rejectedOrderId = createAfterSaleOrder(adminToken, customerToken, courierToken, "STEP04-DECISION-002", "REJECTED");

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-decision", resolvedOrderId)
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "decisionType": "refund",
                                  "decisionAmount": 1.00,
                                  "decisionRemark": "非管理员不得记录"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-decision", rejectedOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "decisionType": "REFUND",
                                  "decisionAmount": 1.00,
                                  "decisionRemark": "拒绝态不允许记录决策"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-decision", resolvedOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "decisionType": "refund",
                                  "decisionAmount": 999.00,
                                  "decisionRemark": "退款金额不能超过订单总额"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-decision", resolvedOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "decisionType": "refund",
                                  "decisionAmount": 3.456,
                                  "decisionRemark": "记录退款决策"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("REFUND", jdbcTemplate.queryForObject(
                "SELECT after_sale_decision_type FROM campus_relay_order WHERE id = ?",
                String.class,
                resolvedOrderId
        ));
        assertEquals(new BigDecimal("3.46"), jdbcTemplate.queryForObject(
                "SELECT after_sale_decision_amount FROM campus_relay_order WHERE id = ?",
                BigDecimal.class,
                resolvedOrderId
        ));
        assertEquals("记录退款决策", jdbcTemplate.queryForObject(
                "SELECT after_sale_decision_remark FROM campus_relay_order WHERE id = ?",
                String.class,
                resolvedOrderId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT after_sale_decided_at FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                resolvedOrderId
        ));

        MvcResult timelineResult = mockMvc.perform(get("/api/campus/admin/orders/{id}/timeline", resolvedOrderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        assertTrue(extractNodeCodes(timelineResult).contains("AFTER_SALE_DECISION_RECORDED"));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-decision", resolvedOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "decisionType": "COMPENSATE",
                                  "decisionAmount": 1.50,
                                  "decisionRemark": "重复记录应拒绝"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void shouldRecordCompensateAndNoneDecision() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");

        String compensateOrderId = createAfterSaleOrder(adminToken, customerToken, courierToken, "STEP04-DECISION-003", "RESOLVED");
        String noneOrderId = createAfterSaleOrder(adminToken, customerToken, courierToken, "STEP04-DECISION-004", "RESOLVED");

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-decision", compensateOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "decisionType": "COMPENSATE",
                                  "decisionAmount": 2.499,
                                  "decisionRemark": "记录补偿决策"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("COMPENSATE", jdbcTemplate.queryForObject(
                "SELECT after_sale_decision_type FROM campus_relay_order WHERE id = ?",
                String.class,
                compensateOrderId
        ));
        assertEquals(new BigDecimal("2.50"), jdbcTemplate.queryForObject(
                "SELECT after_sale_decision_amount FROM campus_relay_order WHERE id = ?",
                BigDecimal.class,
                compensateOrderId
        ));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-decision", noneOrderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "decisionType": "none",
                                  "decisionRemark": "仅记录处理结论"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("NONE", jdbcTemplate.queryForObject(
                "SELECT after_sale_decision_type FROM campus_relay_order WHERE id = ?",
                String.class,
                noneOrderId
        ));
        assertNull(jdbcTemplate.queryForObject(
                "SELECT after_sale_decision_amount FROM campus_relay_order WHERE id = ?",
                BigDecimal.class,
                noneOrderId
        ));
    }

    @Test
    void shouldQuerySettlementDetailAndConfirmWithRemarkAppend() throws Exception {
        String adminToken = loginAdmin();

        mockMvc.perform(get("/api/campus/admin/settlements/{id}", 9999)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));

        mockMvc.perform(get("/api/campus/admin/settlements/{id}", 1)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.relayOrderId").value("CR202604060001"))
                .andExpect(jsonPath("$.data.settlementStatus").value("PENDING"));

        mockMvc.perform(post("/api/campus/admin/settlements/{id}/confirm", 1)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "settleRemark": "   "
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(post("/api/campus/admin/settlements/{id}/confirm", 1)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "settleRemark": "已完成人工打款登记"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("SETTLED", jdbcTemplate.queryForObject(
                "SELECT settlement_status FROM campus_settlement_record WHERE id = 1",
                String.class
        ));
        String remark = jdbcTemplate.queryForObject(
                "SELECT remark FROM campus_settlement_record WHERE id = 1",
                String.class
        );
        assertNotNull(remark);
        assertTrue(remark.endsWith(" | 已完成人工打款登记"));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT settled_at FROM campus_settlement_record WHERE id = 1",
                Timestamp.class
        ));

        mockMvc.perform(post("/api/campus/admin/settlements/{id}/confirm", 1)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "settleRemark": "重复确认"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void shouldQueryCourierRecentExceptionsAndLocationReports() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String orderId = createCampusOrder(customerToken, "STEP04-OPS-001", "LIBRARY", "图书馆", "图书馆南门");

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

        mockMvc.perform(get("/api/campus/admin/couriers/{courierProfileId}/exceptions/recent", 2L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].relayOrderId").value(orderId))
                .andExpect(jsonPath("$.data[0].exceptionType").value("TRAFFIC_DELAY"))
                .andExpect(jsonPath("$.data[0].exceptionReportedAt").isNotEmpty());

        mockMvc.perform(get("/api/campus/admin/couriers/{courierProfileId}/exceptions/recent", 999L)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("limit", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        mockMvc.perform(get("/api/campus/admin/couriers/{courierProfileId}/location-reports", 2L)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("size", "1")
                        .param("relayOrderId", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.size").value(1))
                .andExpect(jsonPath("$.data.records[0].relayOrderId").value(orderId))
                .andExpect(jsonPath("$.data.records[0].note").value("point-b"));

        mockMvc.perform(get("/api/campus/admin/couriers/{courierProfileId}/location-reports", 2L)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("pageSize", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.size").value(100));
    }

    private String createAfterSaleOrder(
            String adminToken,
            String customerToken,
            String courierToken,
            String externalOrderRef,
            String handleAction
    ) throws Exception {
        String orderId = createCampusOrder(customerToken, externalOrderRef, "LIBRARY", "图书馆", "图书馆一楼门口");
        completeOrder(customerToken, courierToken, orderId, "/api/files/" + externalOrderRef + "-proof.jpg");
        openAfterSale(customerToken, orderId, "需要平台介入处理");
        handleAfterSale(adminToken, orderId, handleAction);
        return orderId;
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

    private void completeOrder(String customerToken, String courierToken, String orderId, String pickupProofPath) throws Exception {
        mockPayCampusOrder(customerToken, orderId);
        acceptCampusOrder(courierToken, orderId);
        pickupCampusOrder(courierToken, orderId, pickupProofPath);
        advanceDeliverTwice(courierToken, orderId);
        confirmCampusOrder(customerToken, orderId);
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
                                  "foodDescription": "Step04 联调订单",
                                  "externalPlatformName": "美团",
                                  "externalOrderRef": "%s",
                                  "pickupCode": "S04",
                                  "remark": "Step04 测试订单",
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
