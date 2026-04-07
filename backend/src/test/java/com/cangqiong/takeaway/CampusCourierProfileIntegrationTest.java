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
class CampusCourierProfileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldAllowCourierProfileBridgeWithCustomerToken() throws Exception {
        String customerToken = loginUser("13900139000");

        mockMvc.perform(get("/api/campus/courier/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));

        mockMvc.perform(get("/api/campus/courier/profile")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.phone").value("13900139000"))
                .andExpect(jsonPath("$.data.reviewStatus").value("PENDING"));

        mockMvc.perform(get("/api/campus/courier/review-status")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.profileId").value(1))
                .andExpect(jsonPath("$.data.reviewStatus").value("PENDING"));
    }

    @Test
    void shouldSubmitCourierProfileAndResetReviewStatusToPending() throws Exception {
        String customerToken = loginUser("13900139001");

        mockMvc.perform(post("/api/campus/courier/profile")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "realName": "李四",
                                  "studentNo": "2023123402",
                                  "college": "信息工程学院",
                                  "major": "计算机网络技术",
                                  "className": "网工2302",
                                  "dormitoryBuilding": "梅园",
                                  "dormitoryRoom": "3-301",
                                  "idCardLast4": "5678",
                                  "emergencyContactName": "李母",
                                  "emergencyContactPhone": "13900000002",
                                  "verificationPhotoUrl": "/api/files/courier-lisi-updated-verify.jpg",
                                  "scheduleAttachmentUrl": "/api/files/courier-lisi-updated-schedule.jpg"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(2))
                .andExpect(jsonPath("$.data.reviewStatus").value("PENDING"))
                .andExpect(jsonPath("$.data.enabled").value(0))
                .andExpect(jsonPath("$.data.verificationPhotoUrl").value("/api/files/courier-lisi-updated-verify.jpg"))
                .andExpect(jsonPath("$.data.scheduleAttachmentUrl").value("/api/files/courier-lisi-updated-schedule.jpg"));

        assertEquals("PENDING", jdbcTemplate.queryForObject(
                "SELECT review_status FROM campus_courier_profile WHERE user_id = ?",
                String.class,
                2L
        ));
        assertEquals(Integer.valueOf(0), jdbcTemplate.queryForObject(
                "SELECT enabled FROM campus_courier_profile WHERE user_id = ?",
                Integer.class,
                2L
        ));
        assertEquals("梅园", jdbcTemplate.queryForObject(
                "SELECT dormitory_building FROM campus_courier_profile WHERE user_id = ?",
                String.class,
                2L
        ));
        assertEquals("/api/files/courier-lisi-updated-verify.jpg", jdbcTemplate.queryForObject(
                "SELECT verification_photo_url FROM campus_courier_profile WHERE user_id = ?",
                String.class,
                2L
        ));
    }

    @Test
    void shouldExposeCourierReviewStatusAfterAdminReview() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");

        mockMvc.perform(post("/api/campus/admin/couriers/{id}/review", 1L)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reviewStatus": "APPROVED",
                                  "reviewComment": "资料完整，通过"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/campus/courier/review-status")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.profileId").value(1))
                .andExpect(jsonPath("$.data.reviewStatus").value("APPROVED"))
                .andExpect(jsonPath("$.data.enabled").value(1))
                .andExpect(jsonPath("$.data.reviewedByEmployeeId").value(1))
                .andExpect(jsonPath("$.data.reviewComment").value("资料完整，通过"));

        Timestamp reviewedAt = jdbcTemplate.queryForObject(
                "SELECT reviewed_at FROM campus_courier_profile WHERE id = ?",
                Timestamp.class,
                1L
        );
        assertNotNull(reviewedAt);
    }

    @Test
    void shouldAllowAdminToGetCampusOrderDetail() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");

        mockMvc.perform(get("/api/campus/admin/orders/{id}", "CR202604060001"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));

        mockMvc.perform(get("/api/campus/admin/orders/{id}", "CR202604060001")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(get("/api/campus/admin/orders/{id}", "CR202604060001")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value("CR202604060001"))
                .andExpect(jsonPath("$.data.pickupPointCode").value("NORTH_GATE_LOCKER"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.pickupProofImageUrl").value("/api/files/campus-pickup-proof-001.jpg"))
                .andExpect(jsonPath("$.data.externalOrderRef").value("ELE-20260406-008"))
                .andExpect(jsonPath("$.data.totalAmount").value(6.00))
                .andExpect(jsonPath("$.data.customerRemark").isNotEmpty())
                .andExpect(jsonPath("$.data.courierRemark").isNotEmpty());
    }

    @Test
    void shouldAllowAdminToReviewCourierProfile() throws Exception {
        String adminToken = loginAdmin();
        String customerToken = loginUser("13900139000");

        mockMvc.perform(post("/api/campus/admin/couriers/{id}/review", 1L)
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reviewStatus": "REJECTED",
                                  "reviewComment": "资料照片不清晰，请重新上传"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(post("/api/campus/admin/couriers/{id}/review", 1L)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reviewStatus": "REJECTED",
                                  "reviewComment": "资料照片不清晰，请重新上传"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("REJECTED", jdbcTemplate.queryForObject(
                "SELECT review_status FROM campus_courier_profile WHERE id = ?",
                String.class,
                1L
        ));
        assertEquals(Integer.valueOf(0), jdbcTemplate.queryForObject(
                "SELECT enabled FROM campus_courier_profile WHERE id = ?",
                Integer.class,
                1L
        ));
        assertEquals(Long.valueOf(1L), jdbcTemplate.queryForObject(
                "SELECT reviewed_by_employee_id FROM campus_courier_profile WHERE id = ?",
                Long.class,
                1L
        ));

        mockMvc.perform(get("/api/campus/courier/review-status")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reviewStatus").value("REJECTED"))
                .andExpect(jsonPath("$.data.reviewComment").value("资料照片不清晰，请重新上传"));
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

    private String extractToken(MvcResult result) throws Exception {
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        return response.path("data").path("token").asText();
    }
}
