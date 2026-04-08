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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CampusCourierOnboardingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldAllowCustomerSubmitAndReadOnboardingProfile() throws Exception {
        String customerToken = loginUser("13900139000");

        mockMvc.perform(post("/api/campus/customer/courier-onboarding/profile")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "realName": "张三",
                                  "phone": "13900139000",
                                  "gender": "male",
                                  "studentNo": "2023123401",
                                  "campusZone": "渝中校区",
                                  "dormBuilding": "梅园",
                                  "enabledWorkInOwnBuilding": 1,
                                  "remark": "仅晚间可接单",
                                  "emergencyContactName": "张父",
                                  "emergencyContactPhone": "13900000001"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.phone").value("13900139000"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.campusZone").value("渝中校区"))
                .andExpect(jsonPath("$.data.dormBuilding").value("梅园"))
                .andExpect(jsonPath("$.data.enabledWorkInOwnBuilding").value(1))
                .andExpect(jsonPath("$.data.remark").value("仅晚间可接单"))
                .andExpect(jsonPath("$.data.reviewStatus").value("PENDING"))
                .andExpect(jsonPath("$.data.enabled").value(0));

        mockMvc.perform(get("/api/campus/customer/courier-onboarding/profile")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.dormBuilding").value("梅园"))
                .andExpect(jsonPath("$.data.remark").value("仅晚间可接单"));

        assertEquals("MALE", jdbcTemplate.queryForObject(
                "SELECT gender FROM campus_courier_profile WHERE user_id = ?",
                String.class,
                1L
        ));
        assertEquals("渝中校区", jdbcTemplate.queryForObject(
                "SELECT campus_zone FROM campus_courier_profile WHERE user_id = ?",
                String.class,
                1L
        ));
        assertEquals(Integer.valueOf(1), jdbcTemplate.queryForObject(
                "SELECT enabled_work_in_own_building FROM campus_courier_profile WHERE user_id = ?",
                Integer.class,
                1L
        ));
        assertEquals("仅晚间可接单", jdbcTemplate.queryForObject(
                "SELECT applicant_remark FROM campus_courier_profile WHERE user_id = ?",
                String.class,
                1L
        ));
        assertEquals("PENDING", jdbcTemplate.queryForObject(
                "SELECT review_status FROM campus_courier_profile WHERE user_id = ?",
                String.class,
                1L
        ));
    }

    @Test
    void shouldExposeReviewStatusAndTokenEligibilityFromCustomerOnboardingEntry() throws Exception {
        String approvedCustomerToken = loginUser("13900139001");

        mockMvc.perform(get("/api/campus/customer/courier-onboarding/review-status")
                        .header("Authorization", "Bearer " + approvedCustomerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reviewStatus").value("APPROVED"))
                .andExpect(jsonPath("$.data.enabled").value(1))
                .andExpect(jsonPath("$.data.canApplyCourierToken").value(true));

        mockMvc.perform(get("/api/campus/customer/courier-onboarding/token-eligibility")
                        .header("Authorization", "Bearer " + approvedCustomerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.eligible").value(true))
                .andExpect(jsonPath("$.data.reviewStatus").value("APPROVED"))
                .andExpect(jsonPath("$.data.enabled").value(1))
                .andExpect(jsonPath("$.data.message").value("已通过，可申请 courier token"));
    }

    @Test
    void shouldReturnDefaultOnboardingStateWhenProfileNotSubmitted() throws Exception {
        jdbcTemplate.update("DELETE FROM campus_courier_profile WHERE user_id = ?", 1L);
        String customerToken = loginUser("13900139000");

        mockMvc.perform(get("/api/campus/customer/courier-onboarding/profile")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.phone").value("13900139000"))
                .andExpect(jsonPath("$.data.reviewStatus").doesNotExist());

        mockMvc.perform(get("/api/campus/customer/courier-onboarding/review-status")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.enabled").value(0))
                .andExpect(jsonPath("$.data.canApplyCourierToken").value(false))
                .andExpect(jsonPath("$.data.reviewStatus").doesNotExist())
                .andExpect(jsonPath("$.data.reviewRemark").value("未提交资料"));

        mockMvc.perform(get("/api/campus/customer/courier-onboarding/token-eligibility")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.eligible").value(false))
                .andExpect(jsonPath("$.data.enabled").value(0))
                .andExpect(jsonPath("$.data.reviewStatus").doesNotExist())
                .andExpect(jsonPath("$.data.message").value("未提交资料"));
    }

    @Test
    void shouldKeepOnboardingScopedToCurrentUserAndPreserveOldBridge() throws Exception {
        String customerOneToken = loginUser("13900139000");
        String customerTwoToken = loginUser("13900139001");

        mockMvc.perform(post("/api/campus/customer/courier-onboarding/profile")
                        .header("Authorization", "Bearer " + customerOneToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "realName": "只属于张三的资料",
                                  "phone": "13900139000",
                                  "gender": "MALE",
                                  "studentNo": "2023123401",
                                  "campusZone": "渝中校区",
                                  "dormBuilding": "竹园",
                                  "enabledWorkInOwnBuilding": 0,
                                  "remark": "张三备注",
                                  "emergencyContactName": "张父",
                                  "emergencyContactPhone": "13900000001"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1));

        mockMvc.perform(get("/api/campus/customer/courier-onboarding/profile")
                        .header("Authorization", "Bearer " + customerTwoToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(2))
                .andExpect(jsonPath("$.data.phone").value("13900139001"))
                .andExpect(jsonPath("$.data.reviewStatus").value("APPROVED"));

        mockMvc.perform(get("/api/campus/courier/profile")
                        .header("Authorization", "Bearer " + customerOneToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1));

        mockMvc.perform(get("/api/campus/courier/review-status")
                        .header("Authorization", "Bearer " + customerOneToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
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
