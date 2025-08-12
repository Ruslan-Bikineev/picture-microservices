package edu.school21;

import edu.school21.repository.ImageRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest
public class ApplicationTests {

    protected static Faker faker = new Faker();

    @LocalServerPort
    protected int port;

    @Autowired
    protected ImageRepository imageRepository;

    @BeforeAll
    public static void init() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("http://localhost/api/v1/images")
                .build();
    }
}
