package edu.school21.controller;

import edu.school21.ApplicationTests;
import edu.school21.dto.request.CollectionRqDto;
import edu.school21.dto.request.UserRqDto;
import edu.school21.dto.response.TokenRsDto;
import edu.school21.dto.response.UserCollectionImageRsDto;
import edu.school21.dto.response.UserRsDto;
import edu.school21.entity.Collection;
import edu.school21.entity.CollectionImage;
import edu.school21.entity.User;
import edu.school21.utils.UserHelper;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

class UserControllerTest extends ApplicationTests {

    @Test
    @DisplayName("API. GET. /api/v1/users/{user_id}/collections. Get user collection without token")
    void getUserCollectionsWithoutToken() {
        Integer randomUserId = faker.random().nextInt(1, 5);
        RestAssured.given()
                .port(port)
                .when()
                .get("/{user_id}/collections", randomUserId)
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("path", endsWith("/api/v1/users/%s/collections".formatted(randomUserId)),
                        "message", equalTo("Full authentication is required to access this resource"),
                        "code", equalTo(HTTP_UNAUTHORIZED));
    }

    @Test
    @DisplayName("API. GET. /api/v1/users. Get users without token")
    void getUsersWithoutToken() {
        RestAssured.given()
                .port(port)
                .when()
                .get()
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("path", endsWith("/api/v1/users"),
                        "message", equalTo("Full authentication is required to access this resource"),
                        "code", equalTo(HTTP_UNAUTHORIZED));
    }

    @Test
    @DisplayName("API. GET. /api/v1/users/authorization. Check is user authorized without token")
    void isUserAuthorizeWithoutToken() {
        RestAssured.given()
                .port(port)
                .when()
                .get("/authorization")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/users/authorization"),
                        "message", equalTo("Required request header 'Authorization' for method parameter type String is not present"),
                        "code", equalTo(HTTP_BAD_REQUEST));
    }

    @Test
    @DisplayName("API. POST. /api/v1/users/{user_id}/collections. Post image id without token")
    void postImageToUserCollectionWithoutToken() {
        Integer randomUserId = faker.random().nextInt(1, 5);
        CollectionRqDto collectionRqDto = new CollectionRqDto(faker.number().numberBetween(5L, 10L));
        RestAssured.given()
                .port(port)
                .body(collectionRqDto)
                .when()
                .post("/{user_id}/collections", randomUserId)
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("path", endsWith("/api/v1/users/%s/collections".formatted(randomUserId)),
                        "message", equalTo("Full authentication is required to access this resource"),
                        "code", equalTo(HTTP_UNAUTHORIZED));
    }

    @Test
    @DisplayName("API. DELETE. /api/v1/users/{user_id}/collections. Delete image id without token")
    void deleteImageInUserCollectionWithoutToken() {
        Integer randomUserId = faker.random().nextInt(1, 5);
        CollectionRqDto collectionRqDto = new CollectionRqDto(faker.number().numberBetween(5L, 10L));
        RestAssured.given()
                .port(port)
                .body(collectionRqDto)
                .when()
                .delete("/{user_id}/collections", randomUserId)
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("path", endsWith("/api/v1/users/%s/collections".formatted(randomUserId)),
                        "message", equalTo("Full authentication is required to access this resource"),
                        "code", equalTo(HTTP_UNAUTHORIZED));
    }

    @Test
    @DisplayName("API. POST. /api/v1/users/registration. Register new user")
    void postRegisterNonExistsUser() {
        SoftAssertions sA = new SoftAssertions();
        UserRqDto userRqDto = new UserRqDto(faker.name().firstName(), faker.name().lastName());
        UserHelper.registerUser(port, userRqDto);
        Optional<User> userInDb = userRepository.findByUsername(userRqDto.getUsername());
        sA.assertThat(userInDb.isPresent()).isTrue();
        sA.assertThat(userInDb.get().getUsername()).isEqualTo(userRqDto.getUsername());
        sA.assertThat(passwordEncoder.matches(userRqDto.getPassword(), userInDb.get().getPassword())).isTrue();
        sA.assertAll();
        userRepository.deleteById(userInDb.get().getId());
    }

    @Test
    @DisplayName("API. POST. /api/v1/users/registration. Register already exists user")
    void postRegisterExistsUser() {
        UserRqDto userRqDto = new UserRqDto(faker.name().firstName(), faker.name().lastName());
        UserHelper.registerUser(port, userRqDto);
        RestAssured.given()
                .port(port)
                .body(userRqDto)
                .when()
                .post("/registration")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/users/registration"),
                        "message", equalTo("Пользователь с username %s уже существует".formatted(userRqDto.getUsername())),
                        "code", equalTo(HTTP_BAD_REQUEST));
        userRepository.findByUsername(userRqDto.getUsername())
                .map(User::getId)
                .ifPresent(userRepository::deleteById);
    }

    @Test
    @DisplayName("API. POST. /api/v1/users/authorization. Authorization user with non valid username and valid password")
    void postAuthorizeWithNonValidUsernameAndValidPassword() {
        Integer randomExistsUserId = faker.random().nextInt(1, 5);
        UserRqDto userRqDto = new UserRqDto(faker.name().firstName(), "Pass%s".formatted(randomExistsUserId));
        UserHelper.shouldReturnUnauthorizedWhenUserCredentialsInvalid(port, userRqDto);
    }

    @Test
    @DisplayName("API. POST. /api/v1/users/authorization. Authorization user with valid username and non valid password")
    void postAuthorizeWithValidUsernameAndNonValidPassword() {
        Integer randomExistsUserId = faker.random().nextInt(1, 5);
        UserRqDto userRqDto = new UserRqDto("User%s".formatted(randomExistsUserId), faker.name().lastName());
        UserHelper.shouldReturnUnauthorizedWhenUserCredentialsInvalid(port, userRqDto);
    }

    @Test
    @DisplayName("API. POST. /api/v1/users/authorization. Authorization user with non valid data")
    void postAuthorizeWithNonValidData() {
        UserRqDto userRqDto = new UserRqDto(faker.name().firstName(), faker.name().lastName());
        UserHelper.shouldReturnUnauthorizedWhenUserCredentialsInvalid(port, userRqDto);
    }

    @Nested
    class WithRegisteredUser {
        private Long userId;
        private UserRqDto userRqDto;
        private TokenRsDto tokenRsDto;

        @BeforeEach
        void setUpBeforeEach() {
            userRqDto = new UserRqDto(faker.name().firstName(), faker.name().lastName());
            userId = UserHelper.registerUser(port, userRqDto)
                    .getId();
            tokenRsDto = UserHelper.authorizeUser(port, userRqDto);
        }

        @AfterEach
        void tearDownAfterEach() {
            userRepository.findByUsername(userRqDto.getUsername())
                    .map(User::getId)
                    .ifPresent(userRepository::deleteById);
        }

        @Test
        @DisplayName("API. POST. /api/v1/users/authorization. Authorization user with valid data")
        void postAuthorizeWithValidDataUser() {
            Assertions.assertThat(tokenRsDto.getAccessToken()).isNotNull();
        }

        @Test
        @DisplayName("API. GET. /api/v1/users/authorization. Check auth user with valid token")
        void getCheckAuthUserWithValidToken() {
            RestAssured.given()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(tokenRsDto.getAccessToken()))
                    .port(port)
                    .body(userRqDto)
                    .when()
                    .get("/authorization")
                    .then()
                    .statusCode(HTTP_NO_CONTENT);
            Assertions.assertThat(tokenRsDto.getAccessToken()).isNotNull();
        }

        @Test
        @DisplayName("API. GET. /api/v1/users/authorization. Check auth user when empty token")
        void getCheckAuthUserWhenEmptyToken() {
            RestAssured.given()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                    .port(port)
                    .body(userRqDto)
                    .when()
                    .get("/authorization")
                    .then()
                    .statusCode(HTTP_UNAUTHORIZED)
                    .body("path", endsWith("/api/v1/users/authorization"),
                            "message", equalTo("Строка утверждений JWT пуста: CharSequence cannot be null or empty."),
                            "code", equalTo(HTTP_UNAUTHORIZED));
        }

        @Test
        @DisplayName("API. GET. /api/v1/users/authorization. Check auth user invalid token")
        void getCheckAuthUserWithInvalidToken() {
            RestAssured.given()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer Q%s".formatted(tokenRsDto.getAccessToken()))
                    .port(port)
                    .body(userRqDto)
                    .when()
                    .get("/authorization")
                    .then()
                    .statusCode(HTTP_UNAUTHORIZED)
                    .body("path", endsWith("/api/v1/users/authorization"),
                            "message", startsWith("Недопустимый токен JWT:"),
                            "code", equalTo(HTTP_UNAUTHORIZED));
            Assertions.assertThat(tokenRsDto.getAccessToken()).isNotNull();
        }

        @Test
        @DisplayName("API. GET. /api/v1/users. Get users with valid token and default limit")
        void getUsersWithValidToken() {
            List<UserRsDto> userRsDtoList = RestAssured.given()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(tokenRsDto.getAccessToken()))
                    .port(port)
                    .when()
                    .get()
                    .then()
                    .statusCode(HTTP_OK)
                    .extract()
                    .response()
                    .jsonPath()
                    .getList("", UserRsDto.class);
            List<User> userListInBd = userRepository.findAll();
            IntStream.range(0, 5).forEach(i -> {
                Assertions.assertThat(userListInBd.get(i).getId()).isEqualTo(userRsDtoList.get(i).getId());
                Assertions.assertThat(userListInBd.get(i).getUsername()).isEqualTo(userRsDtoList.get(i).getUsername());
                Assertions.assertThat(userListInBd.get(i).getCollection().getId()).isEqualTo(userRsDtoList.get(i).getCollectionId());
            });
        }

        @Test
        @DisplayName("API. POST. /api/v1/users/{user_id}/collections. Added image id to user collection with valid token")
        void postImageIdToUserCollectionWithValidToken() {
            CollectionRqDto collectionRqDto = new CollectionRqDto(faker.number().numberBetween(1L, 5L));
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
            UserHelper.deleteImageInUserCollection(port, userId, tokenRsDto, collectionRqDto);
        }

        @Test
        @Transactional
        @DisplayName("API. GET. /api/v1/users/{user_id}/collections. Get user collection with valid token")
        void getUserCollectionWithValidToken() {
            SoftAssertions sA = new SoftAssertions();
            CollectionRqDto collectionRqDto = new CollectionRqDto(faker.number().numberBetween(1L, 5L));
            UserHelper.addedImageToUserCollection(port, userId, tokenRsDto, collectionRqDto);
            UserCollectionImageRsDto userCollectionImageRsDto = RestAssured.given()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(tokenRsDto.getAccessToken()))
                    .port(port)
                    .when()
                    .get("/{user_id}/collections", userId)
                    .then()
                    .statusCode(HTTP_OK)
                    .body("collection_id", notNullValue())
                    .extract()
                    .response()
                    .as(UserCollectionImageRsDto.class);
            List<Long> expectedImageIds = userRepository.findById(userId)
                    .map(User::getCollection)
                    .map(Collection::getImages).stream()
                    .flatMap(List::stream)
                    .map(CollectionImage::getImageId)
                    .toList();
            sA.assertThat(userCollectionImageRsDto.getImagesId()).isEqualTo(expectedImageIds);
            sA.assertAll();
            UserHelper.deleteImageInUserCollection(port, userId, tokenRsDto, collectionRqDto);
        }

        @Test
        @DisplayName("API. DELETE. /api/v1/users/{user_id}/collections. Delete image id in user collection with valid token")
        void deleteImageInUserCollectionWithValidToken() {
            CollectionRqDto collectionRqDto = new CollectionRqDto(faker.number().numberBetween(1L, 5L));
            UserHelper.addedImageToUserCollection(port, userId, tokenRsDto, collectionRqDto);
            UserHelper.deleteImageInUserCollection(port, userId, tokenRsDto, collectionRqDto);
        }
    }
}