package com.gongdi.materialpoints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RewardAndReportIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM exchange_order");
        jdbcTemplate.execute("DELETE FROM point_ledger");
        jdbcTemplate.execute("DELETE FROM point_account");
        jdbcTemplate.execute("DELETE FROM recycle_record");
        jdbcTemplate.execute("DELETE FROM reward_item");
        jdbcTemplate.execute("DELETE FROM worker");
        jdbcTemplate.execute("DELETE FROM project");
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
                INSERT INTO app_user (id, username, password, role_code, status, project_id, worker_id)
                VALUES (1, 'admin', '{noop}123456', 'ADMIN', 'ENABLED', 1, NULL)
                """);
        jdbcTemplate.update("""
                INSERT INTO point_account (id, project_id, worker_id, balance)
                VALUES (41, 1, 21, 120),
                       (42, 1, 22, 80)
                """);
        jdbcTemplate.update("""
                INSERT INTO recycle_record (id, project_id, team_id, worker_id, material_item_id, quantity, unit_code, condition_code, calculated_points, status)
                VALUES (51, 1, 1, 21, 1, 35, 'KG', 'OK', 35, 'APPROVED'),
                       (52, 1, 1, 22, 1, 20, 'KG', 'OK', 20, 'SUBMITTED')
                """);
        jdbcTemplate.update("""
                INSERT INTO exchange_order (id, order_no, project_id, worker_id, reward_item_id, quantity, total_points, status, submitted_at, approved_at)
                VALUES (61, 'EX202603230001', 1, 21, 31, 1, 50, 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                       (62, 'EX202603230002', 1, 22, 31, 1, 50, 'SUBMITTED', CURRENT_TIMESTAMP, NULL)
                """);
    }

    @Test
    void rewardItemCreateAndUpdateShouldPass() throws Exception {
        String token = loginAndGetToken("admin", "123456");

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("洗衣液"))
                .andExpect(jsonPath("$.data.pointsCost").value(80));

        mockMvc.perform(put("/api/reward-items/31")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "毛巾礼包",
                                  "pointsCost": 60,
                                  "stock": 8,
                                  "status": "ENABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("毛巾礼包"))
                .andExpect(jsonPath("$.data.stock").value(8));
    }

    @Test
    void reportEndpointsShouldReturnOverviewAndRankings() throws Exception {
        String token = loginAndGetToken("admin", "123456");

        mockMvc.perform(get("/api/reports/overview")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recycleRecordCount").value(2))
                .andExpect(jsonPath("$.data.approvedRecycleCount").value(1))
                .andExpect(jsonPath("$.data.totalAwardedPoints").value(35))
                .andExpect(jsonPath("$.data.exchangeOrderCount").value(2))
                .andExpect(jsonPath("$.data.approvedExchangePoints").value(50))
                .andExpect(jsonPath("$.data.rewardItemCount").value(1));

        mockMvc.perform(get("/api/reports/rankings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].workerId").value(21))
                .andExpect(jsonPath("$.data[0].balance").value(120));

        mockMvc.perform(get("/api/reports/materials")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].materialItemId").value(1))
                .andExpect(jsonPath("$.data[0].totalPoints").value(35));

        mockMvc.perform(get("/api/reports/rewards")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].rewardItemId").value(31))
                .andExpect(jsonPath("$.data[0].totalQuantity").value(1));
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String responseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.path("data").path("token").asText();
    }
}
