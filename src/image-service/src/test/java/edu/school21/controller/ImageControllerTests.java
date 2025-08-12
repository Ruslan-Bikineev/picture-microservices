package edu.school21.controller;

import edu.school21.ApplicationTests;
import edu.school21.dto.request.ImageRqDto;
import edu.school21.entity.Image;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ImageControllerTests extends ApplicationTests {

    @Test
    @DisplayName("API. GET. /api/v1/images/{image_id}. Get exist image by image id")
    void getExistImageByImageId() {
        Long imageId = 1L;
        Image expectedImage = imageRepository.findById(imageId)
                .get();
        RestAssured.given()
                .port(port)
                .when()
                .get("/{image_id}", imageId)
                .then()
                .statusCode(HTTP_OK)
                .body("user_id", equalTo(expectedImage.getUserId().intValue()),
                        "image_base64", equalTo(expectedImage.getImageBase64()));

    }

    @Test
    @DisplayName("API. GET. /api/v1/images/{image_id}. Get exist and deleted image by image id")
    void geExistAndDeletedImageByImageId() {
        Long imageId = 3L;
        RestAssured.given()
                .port(port)
                .when()
                .get("/{image_id}", imageId)
                .then()
                .statusCode(HTTP_NOT_FOUND)
                .body("path", endsWith("/api/v1/images/%s".formatted(imageId)),
                        "message", equalTo("Image with id: %s not found".formatted(imageId)),
                        "code", equalTo(404));

    }

    @Test
    @DisplayName("API. POST. /api/v1/images. Post image")
    void getPostImage() {
        ImageRqDto imageRqDto = new ImageRqDto(faker.name().firstName(), 2L);
        RestAssured.given()
                .port(port)
                .when()
                .body(imageRqDto)
                .post()
                .then()
                .statusCode(HTTP_CREATED)
                .body("user_id", equalTo(imageRqDto.getUserId().intValue()),
                        "image_base64", equalTo(imageRqDto.getImageBase64()));

    }

    @Test
    @DisplayName("API. DELETE. /api/v1/images/{image_id}. Delete image by image id")
    void getDeleteImage() {
        Long imageId = 6L;
        RestAssured.given()
                .port(port)
                .when()
                .delete("/{image_id}", imageId)
                .then()
                .statusCode(HTTP_NO_CONTENT);
        Image actualImage = imageRepository.findById(imageId)
                .get();
        Assertions.assertTrue(actualImage.isDeleted());
    }
}
