package edu.school21.controller;

import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.request.CommentRqDto;
import edu.school21.dto.response.CommentRsDto;
import edu.school21.entity.Comment;
import edu.school21.service.CommentService;
import edu.school21.utils.MapperUtil;
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

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/images")
public class CommentController {

    private final MapperUtil mapperUtil;
    private final CommentService commentService;

    @GeneralApiResponses(summary = "Get all comments by image id")
    @GetMapping("/{image_id}/comments")
    public List<CommentRsDto> getAllCommentsByImageId(@PathVariable("image_id") Long imageId) {
        return commentService.findAllCommentsByImageId(imageId);
    }

    @GeneralApiResponses(summary = "Get comment by image id and comment id")
    @GetMapping("/{image_id}/comments/{comment_id}")
    public CommentRsDto getCommentByImageIdAndCommentId(@PathVariable("image_id") Long imageId,
                                                        @PathVariable("comment_id") Long commentId) {
        Comment comment = commentService.findCommentByImageIdAndUserId(imageId, commentId);
        return mapperUtil.toCommentRsDto(comment);
    }

    @GeneralApiResponses(summary = "Create comment")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{image_id}/comments")
    public CommentRsDto addedComment(@PathVariable("image_id") Long imageId,
                                     @Valid @RequestBody CommentRqDto commentRqDto) {
        Comment comment = mapperUtil.toComment(commentRqDto, imageId);
        comment = commentService.save(comment);
        return mapperUtil.toCommentRsDto(comment);
    }

    @GeneralApiResponses(summary = "Delete comment by image id and comment id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{image_id}/comments/{comment_id}")
    public void deleteCommentByImageIdAndCommentId(@PathVariable("image_id") Long imageId,
                                                   @PathVariable("comment_id") Long commentId) {
        commentService.setDeleted(imageId, commentId);
    }
}
