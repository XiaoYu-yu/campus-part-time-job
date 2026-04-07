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
class CampusCourierAcceptIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldIssueCourierTokenOnlyForApprovedEnabledProfiles() throws Exception {
        mockMvc.perform(post("/api/campus/courier/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"phone":"13900139000","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        MvcResult result = mockMvc.perform(post("/api/campus/courier/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"phone":"13900139001","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.courierProfile.id").value(2))
                .andExpect(jsonPath("$.data.courierProfile.reviewStatus").value("APPROVED"))
                .andReturn();

        assertNotNull(extractToken(result));
    }

    @Test
    void shouldRequireCourierTokenForCourierOrderEndpoints() throws Exception {
        String customerToken = loginUser("13900139001");

        mockMvc.perform(get("/api/campus/courier/orders/available"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));

        mockMvc.perform(get("/api/campus/courier/orders/available")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void shouldListAndAcceptSameBuildingDormitoryOrder() throws Exception {
        String adminToken = loginAdmin();
        String seedOrderId = "CR202604070001";

        mockMvc.perform(post("/api/campus/admin/couriers/{id}/review", 1L)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reviewStatus": "APPROVED",
                                  "reviewComment": "step03c priority approve"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        String courierToken = loginCourier("13900139000");

        mockMvc.perform(get("/api/campus/courier/orders/available")
                        .header("Authorization", "Bearer " + courierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(seedOrderId))
                .andExpect(jsonPath("$.data.records[0].status").value("BUILDING_PRIORITY_PENDING"))
                .andExpect(jsonPath("$.data.records[0].priorityWindowActive").value(true))
                .andExpect(jsonPath("$.data.records[0].pickupPointCode").value("NORTH_GATE_TEMP"));

        mockMvc.perform(get("/api/campus/courier/orders/{id}", seedOrderId)
                        .header("Authorization", "Bearer " + courierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(seedOrderId))
                .andExpect(jsonPath("$.data.priorityWindowActive").value(true));

        mockMvc.perform(post("/api/campus/courier/orders/{id}/accept", seedOrderId)
                        .header("Authorization", "Bearer " + courierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("ACCEPTED", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                seedOrderId
        ));
        assertEquals(Long.valueOf(1L), jdbcTemplate.queryForObject(
                "SELECT courier_profile_id FROM campus_relay_order WHERE id = ?",
                Long.class,
                seedOrderId
        ));
        Timestamp acceptedAt = jdbcTemplate.queryForObject(
                "SELECT accepted_at FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                seedOrderId
        );
        assertNotNull(acceptedAt);
    }

    @Test
    void shouldHideCrossBuildingDormitoryOrderDuringPriorityWindow() throws Exception {
        String customerToken = loginUser("13900139000");
        String courierToken = loginCourier("13900139001");
        String orderId = createCampusOrder(customerToken, "MT-STEP03C-002", "DORMITORY", "竹园", "13900139000");

        mockPayCampusOrder(customerToken, orderId);

        mockMvc.perform(get("/api/campus/courier/orders/available")
                        .header("Authorization", "Bearer " + courierToken)
                        .param("orderNo", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0));

        mockMvc.perform(get("/api/campus/courier/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + courierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(post("/api/campus/courier/orders/{id}/accept", orderId)
                        .header("Authorization", "Bearer " + courierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        assertEquals("BUILDING_PRIORITY_PENDING", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals(0L, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM campus_relay_order WHERE id = ? AND courier_profile_id IS NOT NULL",
                Long.class,
                orderId
        ));
    }

    @Test
    void shouldPreventDuplicateAcceptAfterFirstCourierSucceeds() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");
        String firstCourierToken = loginCourier("13900139001");

        mockMvc.perform(post("/api/campus/admin/couriers/{id}/review", 1L)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reviewStatus": "APPROVED",
                                  "reviewComment": "step03c approve"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        String secondCourierToken = loginCourier("13900139000");
        String orderId = createCampusOrder(customerToken, "ELE-STEP03C-003", "LIBRARY", "图书馆", "13900139000");

        mockPayCampusOrder(customerToken, orderId);

        mockMvc.perform(post("/api/campus/courier/orders/{id}/accept", orderId)
                        .header("Authorization", "Bearer " + firstCourierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/campus/courier/orders/{id}/accept", orderId)
                        .header("Authorization", "Bearer " + secondCourierToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        assertEquals("ACCEPTED", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals(Long.valueOf(2L), jdbcTemplate.queryForObject(
                "SELECT courier_profile_id FROM campus_relay_order WHERE id = ?",
                Long.class,
                orderId
        ));
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
            String contactPhone
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
                                  "contactPhone": "%s",
                                  "foodDescription": "平台联调订单",
                                  "externalPlatformName": "美团",
                                  "externalOrderRef": "%s",
                                  "pickupCode": "S03C",
                                  "remark": "测试订单",
                                  "tipFee": 2.00,
                                  "urgentFlag": 0
                                }
                                """.formatted(
                                targetType,
                                deliveryBuilding,
                                "DORMITORY".equals(targetType) ? deliveryBuilding + "门厅" : "图书馆一楼门口",
                                contactPhone,
                                externalOrderRef)))
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
