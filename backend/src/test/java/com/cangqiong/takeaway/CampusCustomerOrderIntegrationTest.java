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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CampusCustomerOrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldRequireCustomerTokenForCampusCustomerEndpoints() throws Exception {
        String adminToken = loginAdmin();

        mockMvc.perform(get("/api/campus/customer/orders"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));

        mockMvc.perform(get("/api/campus/customer/orders")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void shouldCreateCampusOrderAsUnpaidAndPendingPayment() throws Exception {
        String customerToken = loginUser("13900139000");

        MvcResult result = mockMvc.perform(post("/api/campus/customer/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pickupPointId": 1,
                                  "targetType": "DORMITORY",
                                  "deliveryBuilding": "竹园",
                                  "deliveryDetail": "竹园2栋门口",
                                  "contactName": "张三",
                                  "contactPhone": "13900139000",
                                  "foodDescription": "美团订单：汉堡套餐 + 奶茶",
                                  "externalPlatformName": "美团",
                                  "externalOrderRef": "MT-STEP03A-001",
                                  "pickupCode": "C18",
                                  "remark": "放在门厅即可",
                                  "tipFee": 2.00,
                                  "urgentFlag": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String orderId = extractDataText(result);
        assertNotNull(orderId);
        assertEquals(1L, jdbcTemplate.queryForObject(
                "SELECT customer_user_id FROM campus_relay_order WHERE id = ?",
                Long.class,
                orderId
        ));
        assertEquals("UNPAID", jdbcTemplate.queryForObject(
                "SELECT payment_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals("PENDING_PAYMENT", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals(new BigDecimal("8.00"), jdbcTemplate.queryForObject(
                "SELECT total_amount FROM campus_relay_order WHERE id = ?",
                BigDecimal.class,
                orderId
        ));
    }

    @Test
    void shouldMockPayAndExposeCustomerOrderDetail() throws Exception {
        String customerToken = loginUser("13900139000");
        String orderId = createCampusOrder(customerToken, "MT-STEP03A-002", 1);

        mockMvc.perform(post("/api/campus/customer/orders/{id}/mock-pay", orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("PAID", jdbcTemplate.queryForObject(
                "SELECT payment_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        assertEquals("BUILDING_PRIORITY_PENDING", jdbcTemplate.queryForObject(
                "SELECT order_status FROM campus_relay_order WHERE id = ?",
                String.class,
                orderId
        ));
        Timestamp priorityWindowDeadline = jdbcTemplate.queryForObject(
                "SELECT priority_window_deadline FROM campus_relay_order WHERE id = ?",
                Timestamp.class,
                orderId
        );
        assertNotNull(priorityWindowDeadline);

        mockMvc.perform(get("/api/campus/customer/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(orderId))
                .andExpect(jsonPath("$.data.pickupPointCode").value("NORTH_GATE_TEMP"))
                .andExpect(jsonPath("$.data.paymentStatus").value("PAID"))
                .andExpect(jsonPath("$.data.status").value("BUILDING_PRIORITY_PENDING"))
                .andExpect(jsonPath("$.data.totalAmount").value(8.00));
    }

    @Test
    void shouldListCurrentCustomerOrdersWithFilters() throws Exception {
        String customerToken = loginUser("13900139000");
        String orderId = createCampusOrder(customerToken, "MT-STEP03A-003", 0);

        mockMvc.perform(get("/api/campus/customer/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .param("orderNo", orderId)
                        .param("paymentStatus", "UNPAID")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(orderId))
                .andExpect(jsonPath("$.data.records[0].paymentStatus").value("UNPAID"))
                .andExpect(jsonPath("$.data.records[0].status").value("PENDING_PAYMENT"))
                .andExpect(jsonPath("$.data.records[0].pickupPointCode").value("NORTH_GATE_TEMP"));
    }

    private String createCampusOrder(String customerToken, String externalOrderRef, int urgentFlag) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/campus/customer/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pickupPointId": 1,
                                  "targetType": "DORMITORY",
                                  "deliveryBuilding": "竹园",
                                  "deliveryDetail": "竹园2栋门口",
                                  "contactName": "张三",
                                  "contactPhone": "13900139000",
                                  "foodDescription": "饿了么订单：咖啡 + 面包",
                                  "externalPlatformName": "饿了么",
                                  "externalOrderRef": "%s",
                                  "pickupCode": "A09",
                                  "remark": "放在门厅即可",
                                  "tipFee": 2.00,
                                  "urgentFlag": %d
                                }
                                """.formatted(externalOrderRef, urgentFlag)))
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
        return extractToken(result, "employee");
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
        return extractToken(result, "user");
    }

    private String extractToken(MvcResult result, String ignored) throws Exception {
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        return response.path("data").path("token").asText();
    }

    private String extractDataText(MvcResult result) throws Exception {
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        return response.path("data").asText();
    }
}
