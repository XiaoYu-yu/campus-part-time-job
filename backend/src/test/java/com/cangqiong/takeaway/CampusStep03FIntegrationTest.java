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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CampusStep03FIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldHandleAfterSaleResolvedAsAdminAndShowTimelineNode() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String orderId = createCampusOrder(customerToken, "STEP03F-RESOLVE-001", "LIBRARY", "图书馆", "图书馆一楼门口");

        completeOrder(customerToken, courierToken, orderId, "/api/files/step03f-resolve-proof.jpg");
        openAfterSale(customerToken, orderId, "配送过程发生餐品挤压");

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-handle", orderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "action": "RESOLVED",
                                  "handleRemark": "已与用户确认补偿，售后完结"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("AFTER_SALE_RESOLVED", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals("RESOLVED", jdbcTemplate.queryForObject(
                "SELECT after_sale_handle_action FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals("已与用户确认补偿，售后完结", jdbcTemplate.queryForObject(
                "SELECT after_sale_handle_remark FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT after_sale_handled_at FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                orderId
        ));

        MvcResult timelineResult = mockMvc.perform(get("/api/campus/admin/orders/{id}/timeline", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderStatus").value("AFTER_SALE_RESOLVED"))
                .andReturn();

        assertTrue(extractNodeCodes(timelineResult).contains("AFTER_SALE_RESOLVED"));
    }

    @Test
    void shouldRejectNonAdminAfterSaleHandleAndSupportRejectedTerminalStatus() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String orderId = createCampusOrder(customerToken, "STEP03F-REJECT-001", "LIBRARY", "图书馆", "图书馆二楼门口");

        completeOrder(customerToken, courierToken, orderId, "/api/files/step03f-reject-proof.jpg");
        openAfterSale(customerToken, orderId, "用户坚持认为送达时间超预期");

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-handle", orderId)
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "action": "REJECTED",
                                  "handleRemark": "非管理员不可操作"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-handle", orderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "action": "REJECTED",
                                  "handleRemark": "   "
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(post("/api/campus/admin/orders/{id}/after-sale-handle", orderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "action": "REJECTED",
                                  "handleRemark": "经核查履约链路正常，驳回本次售后"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("AFTER_SALE_REJECTED", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));

        MvcResult timelineResult = mockMvc.perform(get("/api/campus/admin/orders/{id}/timeline", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        assertTrue(extractNodeCodes(timelineResult).contains("AFTER_SALE_REJECTED"));
    }

    @Test
    void shouldReportCourierExceptionWithoutChangingOrderStatusAndExposeTimelineNode() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String orderId = createCampusOrder(customerToken, "STEP03F-EX-001", "LIBRARY", "图书馆", "图书馆一楼门口");

        mockPayCampusOrder(customerToken, orderId);
        acceptCampusOrder(courierToken, orderId);

        mockMvc.perform(post("/api/campus/courier/orders/{id}/exception-report", orderId)
                        .header("Authorization", "Bearer " + courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "exceptionType": "STORE_DELAY",
                                  "exceptionRemark": "商家暂未完成出餐"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("ACCEPTED", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals("STORE_DELAY", jdbcTemplate.queryForObject(
                "SELECT exception_type FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals("商家暂未完成出餐", jdbcTemplate.queryForObject(
                "SELECT exception_remark FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT exception_reported_at FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                orderId
        ));

        mockMvc.perform(get("/api/campus/admin/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.exceptionType").value("STORE_DELAY"))
                .andExpect(jsonPath("$.data.exceptionRemark").value("商家暂未完成出餐"));

        MvcResult timelineResult = mockMvc.perform(get("/api/campus/admin/orders/{id}/timeline", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        assertTrue(extractNodeCodes(timelineResult).contains("EXCEPTION_REPORTED"));
    }

    @Test
    void shouldRejectExceptionReportForAnotherCourierAndValidateRequestFields() throws Exception {
        String adminToken = loginAdmin();
        String ownerToken = loginUser("13900139000");
        String firstCourierToken = loginCourier("13900139001");

        approveCourier(adminToken, 1L, "step03f approve second courier");
        String secondCourierToken = loginCourier("13900139000");
        String orderId = createCampusOrder(ownerToken, "STEP03F-EX-002", "LIBRARY", "图书馆", "图书馆门口");

        mockPayCampusOrder(ownerToken, orderId);
        acceptCampusOrder(firstCourierToken, orderId);

        mockMvc.perform(post("/api/campus/courier/orders/{id}/exception-report", orderId)
                        .header("Authorization", "Bearer " + secondCourierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "exceptionType": "CROSS_COURIER",
                                  "exceptionRemark": "不是我的订单"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(post("/api/campus/courier/orders/{id}/exception-report", orderId)
                        .header("Authorization", "Bearer " + firstCourierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "exceptionType": "",
                                  "exceptionRemark": " "
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void shouldCreateLocationReportWithoutTimelineNoise() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String orderId = createCampusOrder(customerToken, "STEP03F-LOC-001", "LIBRARY", "图书馆", "图书馆一楼门口");

        mockPayCampusOrder(customerToken, orderId);
        acceptCampusOrder(courierToken, orderId);

        mockMvc.perform(post("/api/campus/courier/location-reports")
                        .header("Authorization", "Bearer " + courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "relayOrderId": "%s",
                                  "latitude": 29.5630100,
                                  "longitude": 106.5515500,
                                  "source": "MANUAL",
                                  "note": "位于图书馆南门"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals(1L, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM campus_location_report WHERE relay_order_id = ? AND courier_profile_id = 2",
                Long.class,
                orderId
        ));
        assertEquals("MANUAL", jdbcTemplate.queryForObject(
                "SELECT source FROM campus_location_report WHERE relay_order_id = ? ORDER BY reported_at DESC LIMIT 1",
                String.class,
                orderId
        ));
        assertEquals(new BigDecimal("29.5630100"), jdbcTemplate.queryForObject(
                "SELECT latitude FROM campus_location_report WHERE relay_order_id = ? ORDER BY reported_at DESC LIMIT 1",
                BigDecimal.class,
                orderId
        ));

        MvcResult timelineResult = mockMvc.perform(get("/api/campus/admin/orders/{id}/timeline", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        assertFalse(extractNodeCodes(timelineResult).contains("LOCATION_REPORTED"));
    }

    @Test
    void shouldQueryAdminSettlementsWithPageSizeAndSizeOrderedByCreatedAtDesc() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String newOrderId = createCampusOrder(customerToken, "STEP03F-SETTLEMENT-001", "LIBRARY", "图书馆", "图书馆二楼门口");

        completeOrder(customerToken, courierToken, newOrderId, "/api/files/step03f-settlement-proof.jpg");

        mockMvc.perform(get("/api/campus/admin/settlements")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "1")
                        .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(1))
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.records[0].relayOrderId").value(newOrderId))
                .andExpect(jsonPath("$.data.records[0].settlementStatus").value("PENDING"));

        mockMvc.perform(get("/api/campus/admin/settlements")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("size", "5")
                        .param("relayOrderId", newOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].relayOrderId").value(newOrderId))
                .andExpect(jsonPath("$.data.records[0].courierProfileId").value(2))
                .andExpect(jsonPath("$.data.records[0].grossAmount").value(5.00))
                .andExpect(jsonPath("$.data.records[0].platformCommission").value(0.00))
                .andExpect(jsonPath("$.data.records[0].pendingAmount").value(5.00));
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

    private void approveCourier(String adminToken, Long courierProfileId, String reviewComment) throws Exception {
        mockMvc.perform(post("/api/campus/admin/couriers/{id}/review", courierProfileId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reviewStatus": "APPROVED",
                                  "reviewComment": "%s"
                                }
                                """.formatted(reviewComment)))
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
                                  "foodDescription": "Step03F 联调订单",
                                  "externalPlatformName": "美团",
                                  "externalOrderRef": "%s",
                                  "pickupCode": "S03F",
                                  "remark": "测试订单",
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
