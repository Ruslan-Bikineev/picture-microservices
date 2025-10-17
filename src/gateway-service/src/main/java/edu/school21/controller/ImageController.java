package edu.school21.controller;

import edu.school21.adapters.ImageServiceAdapter;
import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.response.ImageRsDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageServiceAdapter imageServiceAdapter;

    @GeneralApiResponses(summary = "Get image by id")
    @Retryable(retryFor = {WebClientResponseException.TooManyRequests.class,
            WebClientResponseException.ServiceUnavailable.class,
            WebClientResponseException.GatewayTimeout.class},
            backoff = @Backoff(delay = 1000))
    @CircuitBreaker(name = "default-circuit-breaker")
    @GetMapping("/{image_id}")
    public ImageRsDto getImageById(@PathVariable(name = "image_id") Long imageId) {
        return imageServiceAdapter.getImageById(imageId);
    }
}
