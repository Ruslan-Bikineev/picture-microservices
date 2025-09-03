package edu.school21.utils;

import edu.school21.dto.request.CollectionRqDto;
import edu.school21.dto.request.UserRqDto;
import edu.school21.dto.response.MessageRsDto;
import edu.school21.dto.response.TokenRsDto;
import io.restassured.RestAssured;
import org.springframework.http.HttpHeaders;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserHelper {

    public static MessageRsDto registerUser(final Integer port,
                                            final UserRqDto userRqDto) {
        return RestAssured.given()
                .port(port)
                .body(userRqDto)
                .when()
                .post("/registration")
                .then()
                .statusCode(HTTP_CREATED)
                .body("message", equalTo("Пользователь: %s успешно зарегистрирован".formatted(userRqDto.getUsername())))
                .extract()
                .response()
                .as(MessageRsDto.class);
    }

    public static void deleteImageInUserCollection(final Integer port,
                                                   final Long userId,
                                                   final TokenRsDto tokenRsDto,
                                                   final CollectionRqDto collectionRqDto) {
        RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(tokenRsDto.getAccessToken()))
                .port(port)
                .body(collectionRqDto)
                .when()
                .delete("/{user_id}/collections", userId)
                .then()
                .statusCode(HTTP_NO_CONTENT);
    }

    public static TokenRsDto authorizeUser(final Integer port,
                                           final UserRqDto userRqDto) {
        return RestAssured.given()
                .port(port)
                .body(userRqDto)
                .when()
                .post("/authorization")
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .response()
                .as(TokenRsDto.class);
    }

    public static void shouldReturnUnauthorizedWhenUserCredentialsInvalid(final Integer port,
                                                                          final UserRqDto userRqDto) {
        RestAssured.given()
                .port(port)
                .body(userRqDto)
                .when()
                .post("/authorization")
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("path", endsWith("/api/v1/users/authorization"),
                        "message", equalTo("Неверные учетные данные пользователя"),
                        "code", equalTo(HTTP_UNAUTHORIZED));
    }

    public static void addedImageToUserCollection(final Integer port,
                                                  final Long userId,
                                                  final TokenRsDto tokenRsDto,
                                                  final CollectionRqDto collectionRqDto) {
        RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(tokenRsDto.getAccessToken()))
                .port(port)
                .body(collectionRqDto)
                .when()
                .post("/{user_id}/collections", userId)
                .then()
                .statusCode(HTTP_CREATED)
                .body("collection_id", notNullValue(),
                        "image_id", equalTo(collectionRqDto.getImageId().intValue()));
    }
}
