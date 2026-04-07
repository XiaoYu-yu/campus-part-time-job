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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CampusCourierDeliveryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldRejectPickupWithUncontrolledProofPath() throws Exception {
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String orderId = createCampusOrder(customerToken, "STEP03D-PROOF-001", "LIBRARY", "图书馆", "图书馆一楼门口");

        mockPayCampusOrder(customerToken, orderId);
        acceptCampusOrder(courierToken, orderId);

        mockMvc.perform(post("/api/campus/courier/orders/{id}/pickup", orderId)
                        .header("Authorization", "Bearer " + courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pickupProofImageUrl": "C:/temp/proof.jpg",
                                  "courierRemark": "准备取餐"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        assertEquals("ACCEPTED", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
    }

    @Test
    void shouldCompleteMinimalPickupDeliverConfirmFlow() throws Exception {
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String orderId = createCampusOrder(customerToken, "STEP03D-FLOW-001", "LIBRARY", "图书馆", "图书馆二楼门口");

        mockPayCampusOrder(customerToken, orderId);
        acceptCampusOrder(courierToken, orderId);

        mockMvc.perform(post("/api/campus/courier/orders/{id}/pickup", orderId)
                        .header("Authorization", "Bearer " + courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pickupProofImageUrl": "/api/files/campus-pickup-proof-step03d.jpg",
                                  "courierRemark": "已在北门完成取餐"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("PICKED_UP", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals("/api/files/campus-pickup-proof-step03d.jpg", jdbcTemplate.queryForObject(
                "SELECT pickup_proof_image_url FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT picked_up_at FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                orderId
        ));

        mockMvc.perform(get("/api/campus/courier/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + courierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(orderId))
                .andExpect(jsonPath("$.data.status").value("PICKED_UP"))
                .andExpect(jsonPath("$.data.pickupProofImageUrl").value("/api/files/campus-pickup-proof-step03d.jpg"));

        mockMvc.perform(post("/api/campus/courier/orders/{id}/deliver", orderId)
                        .header("Authorization", "Bearer " + courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courierRemark": "正在前往图书馆"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("DELIVERING", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));

        mockMvc.perform(post("/api/campus/courier/orders/{id}/deliver", orderId)
                        .header("Authorization", "Bearer " + courierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courierRemark": "已送达图书馆门口"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("AWAITING_CONFIRMATION", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT delivered_at FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                orderId
        ));

        mockMvc.perform(post("/api/campus/customer/orders/{id}/confirm", orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("COMPLETED", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT auto_complete_at FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                orderId
        ));

        mockMvc.perform(get("/api/campus/customer/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(orderId))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.pickupProofImageUrl").value("/api/files/campus-pickup-proof-step03d.jpg"));
    }

    @Test
    void shouldEnforceCourierAndCustomerOwnershipOnStateProgress() throws Exception {
        String adminToken = loginAdmin();
        String ownerCustomerToken = loginUser("13900139000");
        String otherCustomerToken = loginUser("13900139001");

        mockMvc.perform(post("/api/campus/admin/couriers/{id}/review", 1L)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reviewStatus": "APPROVED",
                                  "reviewComment": "step03d approve second courier"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        String firstCourierToken = loginCourier("13900139001");
        String secondCourierToken = loginCourier("13900139000");
        String orderId = createCampusOrder(ownerCustomerToken, "STEP03D-AUTH-001", "LIBRARY", "图书馆", "图书馆一楼门口");

        mockPayCampusOrder(ownerCustomerToken, orderId);
        acceptCampusOrder(firstCourierToken, orderId);

        mockMvc.perform(post("/api/campus/courier/orders/{id}/pickup", orderId)
                        .header("Authorization", "Bearer " + secondCourierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pickupProofImageUrl": "/api/files/another-proof.jpg"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(post("/api/campus/courier/orders/{id}/pickup", orderId)
                        .header("Authorization", "Bearer " + firstCourierToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pickupProofImageUrl": "/api/files/owner-proof.jpg"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/campus/customer/orders/{id}/confirm", orderId)
                        .header("Authorization", "Bearer " + ownerCustomerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(post("/api/campus/courier/orders/{id}/deliver", orderId)
                        .header("Authorization", "Bearer " + firstCourierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/campus/courier/orders/{id}/deliver", orderId)
                        .header("Authorization", "Bearer " + firstCourierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/campus/customer/orders/{id}/confirm", orderId)
                        .header("Authorization", "Bearer " + otherCustomerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));

        mockMvc.perform(post("/api/campus/customer/orders/{id}/confirm", orderId)
                        .header("Authorization", "Bearer " + ownerCustomerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void acceptCampusOrder(String courierToken, String orderId) throws Exception {
        mockMvc.perform(post("/api/campus/courier/orders/{id}/accept", orderId)
                        .header("Authorization", "Bearer " + courierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void mockPayCampusOrder(String customerToken, String orderId) throws Exception {
        mockMvc.perform(post("/api/campus/customer/orders/{id}/mock-pay", orderId)
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
                                  "foodDescription": "配送闭环联调订单",
                                  "externalPlatformName": "美团",
                                  "externalOrderRef": "%s",
                                  "pickupCode": "S03D",
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
