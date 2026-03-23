package com.gongdi.materialpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PermissionIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM recycle_record");
        jdbcTemplate.execute("DELETE FROM worker");
        jdbcTemplate.execute("DELETE FROM project");
        jdbcTemplate.execute("DELETE FROM reward_item");
        jdbcTemplate.execute("DELETE FROM app_user");

        jdbcTemplate.update("""
                INSERT INTO project (id, name, location, status)
                VALUES (1, '示例项目A', '杭州', 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO worker (id, name, phone, status)
                VALUES (21, '张三', '13800000001', 'ENABLED'),
                       (22, '李四', '13800000002', 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO reward_item (id, name, points_cost, stock, status)
                VALUES (31, '毛巾', 50, 10, 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO recycle_record (id, project_id, team_id, worker_id, material_item_id, quantity, unit_code, condition_code, calculated_points, status)
                VALUES (41, 1, 1, 21, 1, 10, 'KG', 'OK', 10, 'SUBMITTED'),
                       (42, 1, 1, 22, 1, 20, 'KG', 'OK', 20, 'APPROVED')
                """);
        jdbcTemplate.update("""
                INSERT INTO app_user (id, username, password, role_code, status, project_id, worker_id)
                VALUES (1, 'admin', '{noop}123456', 'ADMIN', 'ENABLED', 1, NULL),
                       (2, 'keeper', '{noop}123456', 'KEEPER', 'ENABLED', 1, NULL),
                       (3, 'worker01', '{noop}123456', 'WORKER', 'ENABLED', 1, 21)
                """);
    }

    @Test
    void workerShouldNotAccessAdminConfigApis() throws Exception {
        String token = TestLoginHelper.loginAndGetToken(mockMvc, "worker01", "123456");

        mockMvc.perform(post("/api/reward-items")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "洗衣液",
                                  "pointsCost": 80,
                                  "stock": 20,
                                  "status": "ENABLED"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40301));

        mockMvc.perform(get("/api/reports/overview")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40301));
    }

    @Test
    void workerShouldOnlySeeOwnRecycleRecords() throws Exception {
        String token = TestLoginHelper.loginAndGetToken(mockMvc, "worker01", "123456");

        mockMvc.perform(get("/api/recycle-records")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].workerId").value(21));
    }

    @Test
    void keeperShouldBeAllowedToReviewRecycleRecords() throws Exception {
        String token = TestLoginHelper.loginAndGetToken(mockMvc, "keeper", "123456");

        mockMvc.perform(post("/api/recycle-records/41/approve")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }
}
