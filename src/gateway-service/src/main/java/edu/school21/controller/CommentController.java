package edu.school21.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.school21.adapters.CommentServiceAdapter;
import edu.school21.adapters.ImageServiceAdapter;
import edu.school21.dto.request.CommentRqDto;
import edu.school21.dto.response.CommentRsDto;
import edu.school21.dto.response.ErrorInfoRsDto;
import edu.school21.dto.response.ImageRsDto;
import edu.school21.exception.UnauthorizedCommentDeletionException;
import edu.school21.utils.JwtUtil;
import edu.school21.utils.MapperUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Comment", description = "Comment operations")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/images")
public class CommentController {

    private final JwtUtil jwtUtil;
    private final MapperUtil mapperUtil;
    private final ImageServiceAdapter imageServiceAdapter;
    private final CommentServiceAdapter commentServiceAdapter;

    @Operation(summary = "Get all comments by image id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = CommentRsDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    })
    })
    @Retryable(retryFor = {WebClientResponseException.TooManyRequests.class,
            WebClientResponseException.ServiceUnavailable.class,
            WebClientResponseException.GatewayTimeout.class},
            backoff = @Backoff(delay = 1000))
    @CircuitBreaker(name = "default-circuit-breaker")
    @GetMapping("/{image_id}/comments")
    public List<CommentRsDto> getAllCommentsByImageId(@PathVariable(name = "image_id") Long imageId) {
        return commentServiceAdapter.getAllCommentsByImageId(imageId);
    }

    @Operation(summary = "Add comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentRsDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    })
    })
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

    @Operation(summary = "Delete comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    })
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{image_id}/comments/{comment_id}")
    public void deletedComment(
            HttpServletRequest request,
            @PathVariable(name = "image_id") Long imageId,
            @PathVariable(name = "comment_id") Long commentId) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        String userId = jwtUtil.extractUserId(token);
        ImageRsDto imageById = imageServiceAdapter.getImageById(imageId);
        if (!Objects.equals(imageById.getUserId(), Long.valueOf(userId))) {
            throw new UnauthorizedCommentDeletionException("Нельзя удалить чужой комментарий");
        } else {
            commentServiceAdapter.deletedComment(imageId, commentId);
        }
    }
}
