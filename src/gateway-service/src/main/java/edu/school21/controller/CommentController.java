package edu.school21.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.school21.adapters.CommentServiceAdapter;
import edu.school21.adapters.ImageServiceAdapter;
import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.request.CommentRqDto;
import edu.school21.dto.response.CommentRsDto;
import edu.school21.exception.UnauthorizedCommentDeletionException;
import edu.school21.utils.JwtUtil;
import edu.school21.utils.MapperUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Objects;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/images")
public class CommentController {

    private final JwtUtil jwtUtil;
    private final MapperUtil mapperUtil;
    private final ImageServiceAdapter imageServiceAdapter;
    private final CommentServiceAdapter commentServiceAdapter;

    @GeneralApiResponses(summary = "Get all comments by image id")
    @Retryable(retryFor = {WebClientResponseException.TooManyRequests.class,
            WebClientResponseException.ServiceUnavailable.class,
            WebClientResponseException.GatewayTimeout.class},
            backoff = @Backoff(delay = 1000))
    @CircuitBreaker(name = "default-circuit-breaker")
    @GetMapping("/{image_id}/comments")
    public List<CommentRsDto> getAllCommentsByImageId(@PathVariable(name = "image_id") Long imageId) {
        return commentServiceAdapter.getAllCommentsByImageId(imageId);
    }

    @GeneralApiResponses(summary = "Add comment")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{image_id}/comments")
    public CommentRsDto addedComment(
            HttpServletRequest request,
            @PathVariable(name = "image_id") Long imageId,
            @Valid @RequestBody @NotBlank(message = "comment_text: не может быть пустым")
            @JsonProperty("comment_text") String commentText) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        String userId = jwtUtil.extractUserId(token);
        imageServiceAdapter.getImageById(imageId);
        CommentRqDto commentRqDto = mapperUtil.toCommentRqDto(userId, commentText);
        return commentServiceAdapter.addedComment(imageId, commentRqDto);
    }

    @GeneralApiResponses(summary = "Delete comment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{image_id}/comments/{comment_id}")
    public void deletedComment(
            HttpServletRequest request,
            @PathVariable(name = "image_id") Long imageId,
            @PathVariable(name = "comment_id") Long commentId) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        String userId = jwtUtil.extractUserId(token);
        CommentRsDto commentRsDto = commentServiceAdapter.getCommentByImageIdAndCommentId(imageId, commentId);
        if (!Objects.equals(commentRsDto.getUserId(), Long.valueOf(userId))) {
            throw new UnauthorizedCommentDeletionException("Нельзя удалить чужой комментарий");
        } else {
            commentServiceAdapter.deletedComment(imageId, commentId);
        }
    }
}
