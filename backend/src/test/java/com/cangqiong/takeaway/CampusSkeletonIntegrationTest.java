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
class CampusSkeletonIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldLoadCampusSeedDataOnH2Startup() {
        Long pickupPointCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM campus_pickup_point", Long.class);
        Long relayOrderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM campus_relay_order", Long.class);
        Long courierCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM campus_courier_profile", Long.class);

        assertNotNull(pickupPointCount);
        assertNotNull(relayOrderCount);
        assertNotNull(courierCount);
        assertTrue(pickupPointCount >= 2);
        assertTrue(relayOrderCount >= 2);
        assertTrue(courierCount >= 2);
    }

    @Test
    void shouldAllowCampusPublicEndpointsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/campus/public/pickup-points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].code").value("NORTH_GATE_TEMP"))
                .andExpect(jsonPath("$.data[1].code").value("NORTH_GATE_LOCKER"));

        mockMvc.perform(get("/api/campus/public/delivery-rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.platformName").value("重庆工信职业学院渝中校区校园代送平台"))
                .andExpect(jsonPath("$.data.dormitoryBuildings[0]").value("竹园"))
                .andExpect(jsonPath("$.data.priorityWindowMinutes").value(5))
                .andExpect(jsonPath("$.data.locationReportIntervalSeconds").value(60));
    }

    @Test
    void shouldRequireEmployeeTokenForCampusAdminEndpoints() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser();

        mockMvc.perform(get("/api/campus/admin/orders"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));

        mockMvc.perform(get("/api/campus/admin/couriers")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(get("/api/campus/admin/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("status", "COMPLETED")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value("CR202604060001"))
                .andExpect(jsonPath("$.data.records[0].pickupPointCode").value("NORTH_GATE_LOCKER"))
                .andExpect(jsonPath("$.data.records[0].courierProfileId").value(2))
                .andExpect(jsonPath("$.data.records[0].status").value("COMPLETED"));

        mockMvc.perform(get("/api/campus/admin/couriers")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("reviewStatus", "APPROVED")
                        .param("enabled", "1")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(2))
                .andExpect(jsonPath("$.data.records[0].phone").value("13900139001"))
                .andExpect(jsonPath("$.data.records[0].reviewedByEmployeeId").value(1))
                .andExpect(jsonPath("$.data.records[0].reviewStatus").value("APPROVED"));
    }

    @Test
    void shouldSupportCampusAdminOrderFiltersAndPageMetadata() throws Exception {
        String adminToken = loginAdmin();

        mockMvc.perform(get("/api/campus/admin/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("orderNo", "CR202604070001")
                        .param("deliveryTargetType", "DORMITORY")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value("CR202604070001"))
                .andExpect(jsonPath("$.data.records[0].customerUserId").value(1))
                .andExpect(jsonPath("$.data.records[0].pickupPointCode").value("NORTH_GATE_TEMP"))
                .andExpect(jsonPath("$.data.records[0].paymentStatus").value("PAID"));
    }

    @Test
    void shouldSupportCampusCourierFiltersAndPageMetadata() throws Exception {
        String adminToken = loginAdmin();

        mockMvc.perform(get("/api/campus/admin/couriers")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("studentNo", "2023123401")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(1))
                .andExpect(jsonPath("$.data.records[0].studentNo").value("2023123401"))
                .andExpect(jsonPath("$.data.records[0].reviewStatus").value("PENDING"))
                .andExpect(jsonPath("$.data.records[0].enabled").value(0));
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

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        return response.path("data").path("token").asText();
    }

    private String loginUser() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"phone":"13900139000","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        return response.path("data").path("token").asText();
    }
}
