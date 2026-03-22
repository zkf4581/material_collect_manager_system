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
class RecycleRecordIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM recycle_photo");
        jdbcTemplate.execute("DELETE FROM point_ledger");
        jdbcTemplate.execute("DELETE FROM point_account");
        jdbcTemplate.execute("DELETE FROM recycle_record");
        jdbcTemplate.execute("DELETE FROM file_record");
        jdbcTemplate.execute("DELETE FROM point_rule");
        jdbcTemplate.execute("DELETE FROM material_item");
        jdbcTemplate.execute("DELETE FROM worker");
        jdbcTemplate.execute("DELETE FROM team");
        jdbcTemplate.execute("DELETE FROM project");
        jdbcTemplate.execute("DELETE FROM app_user");

        jdbcTemplate.update("""
                INSERT INTO project (id, name, location, status)
                VALUES (1, '示例项目A', '杭州', 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO team (id, project_id, name, status)
                VALUES (11, 1, '钢筋班组', 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO worker (id, name, phone, status)
                VALUES (21, '张三', '13800000001', 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO material_item (id, name, unit_code, status)
                VALUES (31, '钢筋余料', 'KG', 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO point_rule (id, material_item_id, unit_code, base_point, condition_code, condition_factor, status)
                VALUES (41, 31, 'KG', 1.00, 'OK', 1.00, 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO file_record (id, original_name, file_url, storage_type, mime_type, file_size)
                VALUES (51, 'demo.jpg', '/uploads/2026/03/22/demo.jpg', 'LOCAL', 'image/jpeg', 1024)
                """);
        jdbcTemplate.update("""
                INSERT INTO app_user (id, username, password, role_code, status, project_id)
                VALUES (1, 'keeper', '{noop}123456', 'KEEPER', 'ENABLED', 1)
                """);
    }

    @Test
    void createAndApproveRecycleRecordShouldGeneratePoints() throws Exception {
        String token = loginAndGetToken("keeper", "123456");

        String responseBody = mockMvc.perform(post("/api/recycle-records")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "projectId": 1,
                                  "teamId": 11,
                                  "workerId": 21,
                                  "materialItemId": 31,
                                  "quantity": 35,
                                  "unitCode": "KG",
                                  "conditionCode": "OK",
                                  "remark": "钢筋余料回收",
                                  "photoIds": [51]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.data.calculatedPoints").value(35))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long recycleRecordId = jsonNode.path("data").path("id").asLong();

        mockMvc.perform(post("/api/recycle-records/{id}/approve", recycleRecordId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        Integer balance = jdbcTemplate.queryForObject(
                "SELECT balance FROM point_account WHERE project_id = 1 AND worker_id = 21",
                Integer.class
        );
        Integer ledgerCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM point_ledger WHERE biz_type = 'RECYCLE_AWARD' AND biz_id = ?",
                Integer.class,
                recycleRecordId
        );

        assertThat(balance).isEqualTo(35);
        assertThat(ledgerCount).isEqualTo(1);

        mockMvc.perform(get("/api/recycle-records")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(recycleRecordId));
    }

    @Test
    void createRecycleRecordWithoutPhotoShouldFail() throws Exception {
        String token = loginAndGetToken("keeper", "123456");

        mockMvc.perform(post("/api/recycle-records")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "projectId": 1,
                                  "teamId": 11,
                                  "workerId": 21,
                                  "materialItemId": 31,
                                  "quantity": 35,
                                  "unitCode": "KG",
                                  "conditionCode": "OK",
                                  "remark": "钢筋余料回收",
                                  "photoIds": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(40001));
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
