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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExchangeOrderIntegrationTests {

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
                VALUES (21, '演示工人01', '13800000003', 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO reward_item (id, name, points_cost, stock, status)
                VALUES (31, '毛巾', 50, 10, 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO point_account (id, project_id, worker_id, balance)
                VALUES (41, 1, 21, 120)
                """);
        jdbcTemplate.update("""
                INSERT INTO app_user (id, username, password, role_code, status, project_id, worker_id)
                VALUES (1, 'worker01', '{noop}123456', 'WORKER', 'ENABLED', 1, 21)
                """);
        jdbcTemplate.update("""
                INSERT INTO app_user (id, username, password, role_code, status, project_id, worker_id)
                VALUES (2, 'admin', '{noop}123456', 'ADMIN', 'ENABLED', 1, null)
                """);
    }

    @Test
    void createAndApproveExchangeOrderShouldDeductPointsAndStock() throws Exception {
        String workerToken = loginAndGetToken("worker01", "123456");

        String createResponse = mockMvc.perform(post("/api/exchange-orders")
                        .header("Authorization", "Bearer " + workerToken)
                        .contentType("application/json")
                        .content("""
                                {
                                  "rewardItemId": 31,
                                  "quantity": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.data.totalPoints").value(100))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long orderId = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        mockMvc.perform(post("/api/exchange-orders/{id}/approve", orderId)
                        .header("Authorization", "Bearer " + workerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        Integer balance = jdbcTemplate.queryForObject(
                "SELECT balance FROM point_account WHERE id = 41",
                Integer.class
        );
        Integer stock = jdbcTemplate.queryForObject(
                "SELECT stock FROM reward_item WHERE id = 31",
                Integer.class
        );
        Integer ledgerCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM point_ledger WHERE biz_type = 'EXCHANGE_DEDUCT' AND biz_id = ?",
                Integer.class,
                orderId
        );

        assertThat(balance).isEqualTo(20);
        assertThat(stock).isEqualTo(8);
        assertThat(ledgerCount).isEqualTo(1);

        mockMvc.perform(get("/api/points/summary")
                        .header("Authorization", "Bearer " + workerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(20));

        mockMvc.perform(get("/api/points/ledger")
                        .header("Authorization", "Bearer " + workerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].bizType").value("EXCHANGE_DEDUCT"));
    }

    @Test
    void createExchangeOrderWithInsufficientPointsShouldFailOnApprove() throws Exception {
        jdbcTemplate.update("UPDATE point_account SET balance = 20 WHERE id = 41");
        String workerToken = loginAndGetToken("worker01", "123456");

        String createResponse = mockMvc.perform(post("/api/exchange-orders")
                        .header("Authorization", "Bearer " + workerToken)
                        .contentType("application/json")
                        .content("""
                                {
                                  "rewardItemId": 31,
                                  "quantity": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long orderId = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        mockMvc.perform(post("/api/exchange-orders/{id}/approve", orderId)
                        .header("Authorization", "Bearer " + workerToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40902));
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
