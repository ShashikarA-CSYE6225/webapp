package com.csye6225.webapp;

import com.csye6225.webapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WebAppTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() throws  Exception{
        RestAssured.port = port;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void contextLoads() {
    }

    @Test
    @Order(1)
    void getUser() throws Exception {
        User user = new User();
        user.setUserName("ant.v@live.com");
        user.setFirstName("Shashikar");
        user.setLastName("Anthoni Raj");
        user.setPassword("AV");

        //POST Call
        given()
                .contentType("application/json")
                .body(writeAsJsonString(user))
        .when()
                .post("/v1/user")
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("first_name", equalTo("Shashikar"))
                .body("last_name", equalTo("Anthoni Raj"))
                .body("username", equalTo("ant.v@live.com"))
                .body("account_created", notNullValue())
                .body("account_updated", notNullValue());

        //GET Call
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("ant.v@live.com", "AV");


        given()
                .headers(headers)
        .when()
                .get("/v1/user/self")
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("first_name", equalTo("Shashikar"))
                .body("last_name", equalTo("Anthoni Raj"))
                .body("username", equalTo("ant.v@live.com"))
                .body("account_created", notNullValue())
                .body("account_updated", notNullValue());
    }

    public static String writeAsJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    void updateUser() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("ant.v@live.com", "AV");

        User user = new User();
        user.setFirstName("Shashi");
        user.setLastName("Anthony");
        user.setPassword("AV");

        given()
                .headers(headers)
                .contentType("application/json")
                .body(writeAsJsonString(user))
        .when()
                .put("/v1/user/self")
        .then()
                .statusCode(204);

        headers.setBasicAuth("ant.v@live.com", user.getPassword());

        given()
                .headers(headers)
        .when()
                .get("/v1/user/self")
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("first_name", equalTo(user.getFirstName()))
                .body("last_name", equalTo(user.getLastName()))
                .body("username", equalTo("ant.v@live.com"))
                .body("account_created", notNullValue())
                .body("account_updated", notNullValue());
    }
}
