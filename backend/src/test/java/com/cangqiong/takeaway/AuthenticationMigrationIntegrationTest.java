package com.cangqiong.takeaway;

import com.cangqiong.takeaway.entity.Employee;
import com.cangqiong.takeaway.entity.User;
import com.cangqiong.takeaway.mapper.EmployeeMapper;
import com.cangqiong.takeaway.mapper.UserMapper;
import com.cangqiong.takeaway.utils.MD5Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthenticationMigrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    void employeeLoginShouldUpgradeLegacyPasswordToBcrypt() throws Exception {
        Employee employeeBefore = employeeMapper.selectByPhone("13800138000");
        assertTrue(MD5Util.isLegacyHash(employeeBefore.getPassword()));

        mockMvc.perform(post("/api/employees/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"phone":"13800138000","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Employee employeeAfter = employeeMapper.selectByPhone("13800138000");
        assertFalse(MD5Util.isLegacyHash(employeeAfter.getPassword()));
        assertTrue(employeeAfter.getPassword().startsWith("$2"));
    }

    @Test
    void userLoginShouldUpgradeLegacyPasswordToBcrypt() throws Exception {
        User userBefore = userMapper.selectByPhone("13900139000");
        assertTrue(MD5Util.isLegacyHash(userBefore.getPassword()));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"phone":"13900139000","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        User userAfter = userMapper.selectByPhone("13900139000");
        assertFalse(MD5Util.isLegacyHash(userAfter.getPassword()));
        assertTrue(userAfter.getPassword().startsWith("$2"));
    }
}
