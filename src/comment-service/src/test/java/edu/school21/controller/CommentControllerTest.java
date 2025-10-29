package edu.school21.controller;

import edu.school21.ApplicationTests;
import edu.school21.dto.request.CommentRqDto;
import edu.school21.dto.response.CommentRsDto;
import edu.school21.entity.Comment;
import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

class CommentControllerTest extends ApplicationTests {

    @Test
    @DisplayName("API. GET. /api/v1/images/{image_id}/comments. Get all comments by image id")
    void getAllCommentsByImageId() {
        SoftAssertions sA = new SoftAssertions();
        String imageId = "5";
        List<CommentRsDto> actualList = RestAssured.given()
                .port(port)
                .when()
                .get("/{image_id}/comments", imageId)
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .response()
                .jsonPath()
                .getList("", CommentRsDto.class);
        List<CommentRsDto> expectedList = commentService.findAllByImageId(Long.valueOf(imageId));
        IntStream.range(0, expectedList.size() - 1).forEach(i -> {
            CommentRsDto actualCommentRsDto = actualList.get(i);
            CommentRsDto expectedComment = expectedList.get(i);
            sA.assertThat(actualCommentRsDto).isEqualTo(expectedComment);
        });
        sA.assertAll();
    }

    @Test
    @DisplayName("API. GET. /api/v1/images/{image_id}/comments/{comment_id}. Get deleted comment by image id and comment id")
    void getDeletedCommentByImageIdAndCommentId() {
        String commentId = "6";
        String imageId = "5";
        RestAssured.given()
                .port(port)
                .when()
                .get("/{image_id}/comments/{comment_id}", imageId, commentId)
                .then()
                .statusCode(HTTP_NOT_FOUND)
                .body("path", endsWith("/api/v1/images/%s/comments/%s".formatted(imageId, commentId)),
                        "message", equalTo("Comment with id: %s and image id: %s not found".formatted(commentId, imageId)),
                        "code", equalTo(404));
    }

    @Test
    @DisplayName("API. POST. /api/v1/images/{image_id}/comments. Create comment by image id with exist user id")
    void postCreateCommentWithExistUserId() {
        Long imageId = 3L;
        CommentRqDto commentRqDto = new CommentRqDto(faker.lorem().sentence(), 1L);
        long commentId = RestAssured.given()
                .port(port)
                .when()
                .body(commentRqDto)
                .post("/{image_id}/comments", imageId)
                .then()
                .statusCode(HTTP_CREATED)
                .body("image_id", equalTo(imageId.intValue()),
                        "user_id", equalTo((commentRqDto.getUserId().intValue())),
                        "comment_text", equalTo(commentRqDto.getCommentText()))
                .extract()
                .jsonPath()
                .getLong("id");
        Assertions.assertTrue(commentRepository.findById(commentId).isPresent());
    }

    @Test
    @DisplayName("API. DELETE. /api/v1/{image_id}/comments/{comment_id}. Delete comment by image id and comment id")
    void deleteExistComment() {
        Long imageId = 3L;
        Long randomUserId = faker.number().numberBetween(1L, 5L);
        CommentRqDto commentRqDto = new CommentRqDto(faker.lorem().sentence(), randomUserId);
        RestAssured.given()
                .port(port)
                .when()
                .body(commentRqDto)
                .post("/{image_id}/comments", imageId)
                .then()
                .statusCode(HTTP_CREATED);
        CommentRsDto createdComment = commentService.findAllByImageId(imageId).stream()
                .max(Comparator.comparing(CommentRsDto::getId))
                .get();
        RestAssured.given()
                .port(port)
                .when()
                .body(commentRqDto)
                .delete("/{image_id}/comments/{comment_id}", imageId, createdComment.getId())
                .then()
                .statusCode(HTTP_NO_CONTENT);
        Comment deletedComment = commentRepository.findById(createdComment.getId())
                .get();
        Assertions.assertTrue(deletedComment.isDeleted());
    }
}