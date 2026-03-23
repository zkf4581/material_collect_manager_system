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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectWorkerManageIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM team");
        jdbcTemplate.execute("DELETE FROM worker");
        jdbcTemplate.execute("DELETE FROM project");
        jdbcTemplate.execute("DELETE FROM app_user");

        jdbcTemplate.update("""
                INSERT INTO app_user (id, username, password, role_code, status, project_id, worker_id)
                VALUES (1, 'admin', '{noop}123456', 'ADMIN', 'ENABLED', 1, NULL)
                """);
        jdbcTemplate.update("""
                INSERT INTO project (id, name, location, status)
                VALUES (11, '示例项目A', '杭州', 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO team (id, project_id, name, status)
                VALUES (21, 11, '木工班组', 'ENABLED')
                """);
        jdbcTemplate.update("""
                INSERT INTO worker (id, name, phone, status)
                VALUES (31, '张三', '13800000001', 'ENABLED')
                """);
    }

    @Test
    void createAndUpdateProjectShouldPass() throws Exception {
        String token = TestLoginHelper.loginAndGetToken(mockMvc, "admin", "123456");

        mockMvc.perform(post("/api/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "示例项目B",
                                  "location": "宁波",
                                  "status": "ENABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("示例项目B"));

        mockMvc.perform(put("/api/projects/11")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "示例项目A-更新",
                                  "location": "杭州余杭",
                                  "status": "DISABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("示例项目A-更新"))
                .andExpect(jsonPath("$.data.status").value("DISABLED"));
    }

    @Test
    void createAndUpdateTeamAndWorkerShouldPass() throws Exception {
        String token = TestLoginHelper.loginAndGetToken(mockMvc, "admin", "123456");

        mockMvc.perform(post("/api/teams")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "projectId": 11,
                                  "name": "钢筋班组",
                                  "status": "ENABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("钢筋班组"));

        mockMvc.perform(put("/api/teams/21")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "projectId": 11,
                                  "name": "木工班组-更新",
                                  "status": "DISABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("木工班组-更新"));

        mockMvc.perform(post("/api/workers")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "李四",
                                  "phone": "13800000002",
                                  "status": "ENABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("李四"));

        mockMvc.perform(put("/api/workers/31")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "张三-更新",
                                  "phone": "13800000009",
                                  "status": "DISABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("张三-更新"))
                .andExpect(jsonPath("$.data.status").value("DISABLED"));

        mockMvc.perform(get("/api/workers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }
}
