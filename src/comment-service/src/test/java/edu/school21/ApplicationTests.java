package edu.school21;

import edu.school21.repository.CommentRepository;
import edu.school21.service.CommentService;
import edu.school21.utils.BaseTestContainers;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests extends BaseTestContainers {

    @LocalServerPort
    protected int port;

    protected static Faker faker = new Faker();

    @Autowired
    protected CommentService commentService;

    @Autowired
    protected CommentRepository commentRepository;

    @BeforeAll
    static void init() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("http://localhost/api/v1/images")
                .build();
    }

    @Test
    @DisplayName("Context loads")
    void contextLoads() {
        assertTrue(true);
    }
}