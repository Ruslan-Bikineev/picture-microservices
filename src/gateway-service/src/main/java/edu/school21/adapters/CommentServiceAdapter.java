package edu.school21.adapters;

import edu.school21.dto.request.CommentRqDto;
import edu.school21.dto.response.CommentRsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class CommentServiceAdapter {

    private final WebClient webClient;

    public CommentServiceAdapter(@Value("${service.url.comment-service}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public List<CommentRsDto> getAllCommentsByImageId(Long imageId) {
        return webClient
                .method(HttpMethod.GET)
                .uri("/{image_id}/comments", imageId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CommentRsDto>>() {
                })
                .block();
    }

    public CommentRsDto getCommentByImageIdAndCommentId(Long imageId, Long commentId) {
        return webClient
                .method(HttpMethod.GET)
                .uri("/{image_id}/comments/{comment_id}", imageId, commentId)
                .retrieve()
                .bodyToMono(CommentRsDto.class)
                .block();
    }

    public CommentRsDto addedComment(Long imageId, CommentRqDto commentRqDto) {
        return webClient
                .method(HttpMethod.POST)
                .uri("/{image_id}/comments", imageId)
                .bodyValue(commentRqDto)
                .retrieve()
                .bodyToMono(CommentRsDto.class)
                .block();
    }

    public void deletedComment(Long imageId, Long commentId) {
        webClient
                .method(HttpMethod.DELETE)
                .uri("/{image_id}/comments/{comment_id}", imageId, commentId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
