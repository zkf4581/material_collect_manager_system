package com.gongdi.materialpoints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class TestLoginHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TestLoginHelper() {
    }

    public static String loginAndGetToken(MockMvc mockMvc, String username, String password) throws Exception {
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
        JsonNode jsonNode = OBJECT_MAPPER.readTree(responseBody);
        return jsonNode.path("data").path("token").asText();
    }
}
