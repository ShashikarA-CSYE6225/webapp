package com.csye6225.webapp;

import com.csye6225.webapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WebAppTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void getUser() throws Exception {
        User user = new User();
        user.setUserName("ant.v@live.com");
        user.setFirstName("Shashikar");
        user.setLastName("Anthoni Raj");
        user.setPassword("AV");
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/user")
                        .content(writeAsJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("ant.v@live.com", "AV");

        mockMvc.perform(get("/v1/user/self")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().json("{'first_name': 'Shashikar','last_name': 'Anthoni Raj', 'username': 'ant.v@live.com'}"));
    }

    @Test
    void updateUser() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("ant.v@live.com", "AV");

        User user = new User();
        user.setFirstName("Shashi");
        user.setLastName("Anthony");
        user.setPassword("AV");

        mockMvc.perform(put("/v1/user/self")
                        .headers(headers)
                        .content(writeAsJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        headers.setBasicAuth("ant.v@live.com", user.getPassword());

        mockMvc.perform(get("/v1/user/self")
                        .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().json("{'first_name': 'Shashi','last_name': 'Anthony', 'username': 'ant.v@live.com'}"));
    }

    public static String writeAsJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
