package edu.school21.controller;

import edu.school21.dto.request.CommentRqDto;
import edu.school21.dto.response.CommentRsDto;
import edu.school21.dto.response.ErrorInfoRsDto;
import edu.school21.entity.Comment;
import edu.school21.service.CommentService;
import edu.school21.utils.MapperUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Comment", description = "Comment operations")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/images")
public class CommentController {

    private final MapperUtil mapperUtil;
    private final CommentService commentService;

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
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    })
    })
    @GetMapping("/{image_id}/comments")
    public List<CommentRsDto> getAllCommentsByImageId(@PathVariable("image_id") Long imageId) {
        return commentService.findAllCommentsByImageId(imageId);
    }

    @Operation(summary = "Get comment by image id and comment id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentRsDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    })
    })
    @GetMapping("/{image_id}/comments/{comment_id}")
    public CommentRsDto getCommentByImageIdAndCommentId(@PathVariable("image_id") Long imageId,
                                                        @PathVariable("comment_id") Long commentId) {
        Comment comment = commentService.findCommentByImageIdAndUserId(imageId, commentId);
        return mapperUtil.toCommentRsDto(comment);
    }

    @Operation(summary = "Create comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentRsDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    })
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{image_id}/comments")
    public CommentRsDto addedComment(@PathVariable("image_id") Long imageId,
                                     @Valid @RequestBody CommentRqDto commentRqDto) {
        Comment comment = mapperUtil.toComment(commentRqDto, imageId);
        comment = commentService.save(comment);
        return mapperUtil.toCommentRsDto(comment);
    }

    @Operation(summary = "Delete comment by image id and comment id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "400", description = "Bad request",
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
    public void deleteCommentByImageIdAndCommentId(@PathVariable("image_id") Long imageId,
                                                   @PathVariable("comment_id") Long commentId) {
        commentService.setDeleted(imageId, commentId);
    }
}
