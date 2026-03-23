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
class MaterialManageIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM point_rule");
        jdbcTemplate.execute("DELETE FROM material_item");
        jdbcTemplate.execute("DELETE FROM app_user");

        jdbcTemplate.update("""
                INSERT INTO app_user (id, username, password, role_code, status, project_id, worker_id)
                VALUES (1, 'admin', '{noop}123456', 'ADMIN', 'ENABLED', 1, NULL)
                """);
        jdbcTemplate.update("""
                INSERT INTO material_item (id, name, unit_code, status)
                VALUES (11, '木方', 'GEN', 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO point_rule (id, material_item_id, unit_code, base_point, condition_code, condition_factor, status)
                VALUES (21, 11, 'GEN', 2.00, 'OK', 1.00, 'ENABLED')
                """);
    }

    @Test
    void createAndUpdateMaterialItemShouldPass() throws Exception {
        String token = loginAndGetToken("admin", "123456");

        mockMvc.perform(post("/api/material-items")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "模板",
                                  "unitCode": "ZHANG",
                                  "status": "ENABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("模板"))
                .andExpect(jsonPath("$.data.unitCode").value("ZHANG"));

        mockMvc.perform(put("/api/material-items/11")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "木方-更新",
                                  "unitCode": "GEN",
                                  "status": "DISABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("木方-更新"))
                .andExpect(jsonPath("$.data.status").value("DISABLED"));
    }

    @Test
    void createAndUpdatePointRuleShouldPass() throws Exception {
        String token = loginAndGetToken("admin", "123456");

        String createResponse = mockMvc.perform(post("/api/point-rules")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "materialItemId": 11,
                                  "unitCode": "GEN",
                                  "basePoint": 3.50,
                                  "conditionCode": "MINOR",
                                  "conditionFactor": 0.80,
                                  "status": "ENABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.basePoint").value(3.50))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(createResponse);
        long ruleId = jsonNode.path("data").path("id").asLong();

        mockMvc.perform(put("/api/point-rules/{id}", ruleId)
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "materialItemId": 11,
                                  "unitCode": "GEN",
                                  "basePoint": 4.00,
                                  "conditionCode": "OK",
                                  "conditionFactor": 1.00,
                                  "status": "ENABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.basePoint").value(4.00))
                .andExpect(jsonPath("$.data.conditionCode").value("OK"));

        mockMvc.perform(get("/api/point-rules")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
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
