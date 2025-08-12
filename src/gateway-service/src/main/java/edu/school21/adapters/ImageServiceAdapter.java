package edu.school21.adapters;

import edu.school21.dto.request.ImageRqDto;
import edu.school21.dto.response.ImageRsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ImageServiceAdapter {

    private final WebClient webClient;

    public ImageServiceAdapter(@Value("${service.url.image-service}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public ImageRsDto addedImage(ImageRqDto imageRqDto) {
        return webClient
                .method(HttpMethod.POST)
                .bodyValue(imageRqDto)
                .retrieve()
                .bodyToMono(ImageRsDto.class)
                .block();
    }

    public ImageRsDto getImageById(Long imageId) {
        return webClient
                .method(HttpMethod.GET)
                .uri("/{image_id}", imageId)
                .retrieve()
                .bodyToMono(ImageRsDto.class)
                .block();
    }

    public void deletedImageById(Long imageId) {
        webClient
                .method(HttpMethod.DELETE)
                .uri("/{image_id}", imageId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
