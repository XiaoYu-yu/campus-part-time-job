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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CampusFeedbackIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldSubmitFeedbackAndAllowAdminToProcessIt() throws Exception {
        String requestBody = """
                {
                  "submitterRole": "USER",
                  "category": "BUG",
                  "content": "订单详情页偶尔需要手动刷新",
                  "contact": "13900139000",
                  "page": "/user/campus/order-result",
                  "orderId": "CR202604060001"
                }
                """;

        MvcResult submitResult = mockMvc.perform(post("/api/campus/public/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        long feedbackId = extractDataNode(submitResult).asLong();
        assertEquals("PENDING", jdbcTemplate.queryForObject(
                "SELECT status FROM campus_feedback WHERE id = ?",
                String.class,
                feedbackId
        ));

        mockMvc.perform(post("/api/campus/public/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(429))
                .andExpect(jsonPath("$.msg").value("相同反馈刚刚已经提交，请稍后再试"));

        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM campus_feedback WHERE category = 'BUG' AND content = ?",
                Integer.class,
                "订单详情页偶尔需要手动刷新"
        ));

        String customerToken = loginUser();
        mockMvc.perform(get("/api/campus/admin/feedback")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        String adminToken = loginAdmin();
        mockMvc.perform(get("/api/campus/admin/feedback")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("status", "PENDING")
                        .param("category", "BUG")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(feedbackId))
                .andExpect(jsonPath("$.data.records[0].orderId").value("CR202604060001"));

        mockMvc.perform(get("/api/campus/admin/feedback/{id}", feedbackId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("订单详情页偶尔需要手动刷新"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        mockMvc.perform(post("/api/campus/admin/feedback/{id}/process", feedbackId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS",
                                  "adminNote": "已复现，正在排查刷新时机。"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertEquals("IN_PROGRESS", jdbcTemplate.queryForObject(
                "SELECT status FROM campus_feedback WHERE id = ?",
                String.class,
                feedbackId
        ));
        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT processed_at FROM campus_feedback WHERE id = ?",
                Object.class,
                feedbackId
        ));

        mockMvc.perform(post("/api/campus/admin/feedback/{id}/process", feedbackId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "RESOLVED",
                                  "adminNote": "已确认现有轮询可恢复，记录为后续体验优化。"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/campus/admin/feedback/{id}", feedbackId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("RESOLVED"))
                .andExpect(jsonPath("$.data.processedByEmployeeId").value(1))
                .andExpect(jsonPath("$.data.adminNote").value("已确认现有轮询可恢复，记录为后续体验优化。"));

        mockMvc.perform(post("/api/campus/admin/feedback/{id}/process", feedbackId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "RESOLVED",
                                  "adminNote": "重复处理"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
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
        return extractDataNode(result).path("token").asText();
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
        return extractDataNode(result).path("token").asText();
    }

    private JsonNode extractDataNode(MvcResult result) throws Exception {
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        return response.path("data");
    }
}
