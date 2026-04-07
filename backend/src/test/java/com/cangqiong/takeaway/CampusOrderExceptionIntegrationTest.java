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

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
class CampusOrderExceptionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldAllowCustomerCancelBeforePickupAndExposeAdminTimeline() throws Exception {
        String adminToken = loginAdmin();
        String ownerToken = loginUser("13900139000");
        String otherCustomerToken = loginUser("13900139001");
        String orderId = createCampusOrder(ownerToken, "STEP03E-CANCEL-001", "LIBRARY", "图书馆", "图书馆一楼门口");

        mockPayCampusOrder(ownerToken, orderId);

        mockMvc.perform(post("/api/campus/customer/orders/{id}/cancel", orderId)
                        .header("Authorization", "Bearer " + otherCustomerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cancelReason": "非本人订单"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));

        mockMvc.perform(post("/api/campus/customer/orders/{id}/cancel", orderId)
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cancelReason": "临时有课，无法收餐"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("CANCELLED", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals("临时有课，无法收餐", jdbcTemplate.queryForObject(
                "SELECT cancel_reason FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT cancelled_at FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                orderId
        ));

        mockMvc.perform(get("/api/campus/customer/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"))
                .andExpect(jsonPath("$.data.cancelReason").value("临时有课，无法收餐"));

        mockMvc.perform(get("/api/campus/admin/orders/{id}/timeline", orderId))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));

        mockMvc.perform(get("/api/campus/admin/orders/{id}/timeline", orderId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(get("/api/campus/admin/orders/{id}/timeline", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.orderStatus").value("CANCELLED"))
                .andExpect(jsonPath("$.data.items[0].nodeCode").value("CREATED"))
                .andExpect(jsonPath("$.data.items[1].nodeCode").value("PAID"))
                .andExpect(jsonPath("$.data.items[2].nodeCode").value("CANCELLED"));
    }

    @Test
    void shouldRejectCustomerCancelAfterPickup() throws Exception {
        String ownerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String orderId = createCampusOrder(ownerToken, "STEP03E-CANCEL-002", "LIBRARY", "图书馆", "图书馆一楼门口");

        mockPayCampusOrder(ownerToken, orderId);
        acceptCampusOrder(courierToken, orderId);
        pickupCampusOrder(courierToken, orderId, "/api/files/campus-step03e-cancel-proof.jpg");

        mockMvc.perform(post("/api/campus/customer/orders/{id}/cancel", orderId)
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cancelReason": "已经不需要了"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        assertEquals("PICKED_UP", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
    }

    @Test
    void shouldOpenAfterSaleAndGenerateSettlementForCompletedOrder() throws Exception {
        String adminToken = loginAdmin();
        String ownerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String orderId = createCampusOrder(ownerToken, "STEP03E-AFTERSALE-001", "LIBRARY", "图书馆", "图书馆二楼门口");

        mockPayCampusOrder(ownerToken, orderId);
        acceptCampusOrder(courierToken, orderId);
        pickupCampusOrder(courierToken, orderId, "/api/files/campus-step03e-aftersale-proof.jpg");
        advanceDeliverTwice(courierToken, orderId);
        confirmCampusOrder(ownerToken, orderId);

        assertEquals("COMPLETED", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals("PENDING", jdbcTemplate.queryForObject(
                "SELECT settlement_status FROM campus_settlement_record WHERE relay_order_id = ?",
                String.class,
                orderId
        ));
        assertEquals("订单完成待结算", jdbcTemplate.queryForObject(
                "SELECT remark FROM campus_settlement_record WHERE relay_order_id = ?",
                String.class,
                orderId
        ));

        mockMvc.perform(post("/api/campus/customer/orders/{id}/after-sale", orderId)
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "afterSaleReason": "餐品洒漏，需要人工介入"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("AFTER_SALE_OPEN", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals("餐品洒漏，需要人工介入", jdbcTemplate.queryForObject(
                "SELECT after_sale_reason FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT after_sale_applied_at FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                orderId
        ));

        mockMvc.perform(get("/api/campus/customer/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("AFTER_SALE_OPEN"))
                .andExpect(jsonPath("$.data.afterSaleReason").value("餐品洒漏，需要人工介入"));

        MvcResult timelineResult = mockMvc.perform(get("/api/campus/admin/orders/{id}/timeline", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderStatus").value("AFTER_SALE_OPEN"))
                .andExpect(jsonPath("$.data.settlementStatus").value("PENDING"))
                .andReturn();

        JsonNode items = extractDataNode(timelineResult).path("items");
        List<String> nodeCodes = objectMapper.convertValue(
                items.findValuesAsText("nodeCode"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );
        assertTrue(nodeCodes.contains("CREATED"));
        assertTrue(nodeCodes.contains("PAID"));
        assertTrue(nodeCodes.contains("ACCEPTED"));
        assertTrue(nodeCodes.contains("PICKED_UP"));
        assertTrue(nodeCodes.contains("DELIVERED"));
        assertTrue(nodeCodes.contains("CONFIRMED"));
        assertTrue(nodeCodes.contains("SETTLEMENT_PENDING"));
        assertTrue(nodeCodes.contains("AFTER_SALE_OPENED"));
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
                                  "courierRemark": "已在北门完成取餐"
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
                                  "courierRemark": "已送达指定地点"
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
                                  "foodDescription": "Step03E 异常处理联调订单",
                                  "externalPlatformName": "美团",
                                  "externalOrderRef": "%s",
                                  "pickupCode": "S03E",
                                  "remark": "按约定地点交付",
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
