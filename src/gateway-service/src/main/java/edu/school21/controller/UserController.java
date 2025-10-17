package edu.school21.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.school21.adapters.ImageServiceAdapter;
import edu.school21.adapters.UserServiceAdapter;
import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.request.CollectionRqDto;
import edu.school21.dto.request.ImageRqDto;
import edu.school21.dto.response.CollectionRsDto;
import edu.school21.dto.response.ImageRsDto;
import edu.school21.dto.response.UserCollectionImageRsDto;
import edu.school21.dto.response.UserRsDto;
import edu.school21.utils.JwtUtil;
import edu.school21.utils.MapperUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final JwtUtil jwtUtil;
    private final MapperUtil mapperUtil;
    private final UserServiceAdapter userServiceAdapter;
    private final ImageServiceAdapter imageServiceAdapter;

    @GeneralApiResponses(summary = "Save image to collection")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/me/collections")
    public CollectionRsDto addedImage(
            HttpServletRequest request,
            @Valid @RequestBody @NotBlank(message = "image_base64: не может быть пустым")
            @JsonProperty("image_base64") String imageBase64) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        String userId = jwtUtil.extractUserId(token);
        ImageRqDto imageRqDto = mapperUtil.toImageRqDto(userId, imageBase64);
        ImageRsDto imageRsDto = imageServiceAdapter.addedImage(imageRqDto);
        CollectionRqDto collectionRqDto = mapperUtil.toCollectionRqDto(imageRsDto.getId());
        try {
            return userServiceAdapter.addedImageToUserCollection(token, userId, collectionRqDto);
        } catch (Exception ex) {
            imageServiceAdapter.deletedImageById(imageRsDto.getId());
            throw ex;
        }
    }

    @GeneralApiResponses(summary = "Get user collection")
    @Retryable(retryFor = {WebClientResponseException.TooManyRequests.class,
            WebClientResponseException.ServiceUnavailable.class,
            WebClientResponseException.GatewayTimeout.class},
            backoff = @Backoff(delay = 1000))
    @CircuitBreaker(name = "default-circuit-breaker")
    @GetMapping("/{user_id}/collections")
    public UserCollectionImageRsDto getUserCollection(
            HttpServletRequest request,
            @PathVariable(name = "user_id") Long userId) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        return userServiceAdapter.getUserCollection(token, userId);
    }

    @GeneralApiResponses(summary = "Get users")
    @Retryable(retryFor = {WebClientResponseException.TooManyRequests.class,
            WebClientResponseException.ServiceUnavailable.class,
            WebClientResponseException.GatewayTimeout.class},
            backoff = @Backoff(delay = 1000))
    @CircuitBreaker(name = "default-circuit-breaker")
    @GetMapping
    public List<UserRsDto> getUsers(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "must be greater than 0")
            @Max(value = 20, message = "must be less than 21")
            Integer limit,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "must be positive")
            Integer offset) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        return userServiceAdapter.getUsers(token, limit, offset);
    }

    @GeneralApiResponses(summary = "Delete image from user collection")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/me/collections")
    public void deleteImage(
            HttpServletRequest request,
            @Valid @RequestBody CollectionRqDto collectionRqDto) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        String userId = jwtUtil.extractUserId(token);
        userServiceAdapter.deleteImageInUserCollection(token, userId, collectionRqDto);
        try {
            imageServiceAdapter.deletedImageById(collectionRqDto.getImageId());
        } catch (Exception ex) {
            userServiceAdapter.addedImageToUserCollection(token, userId, collectionRqDto);
            throw ex;
        }
    }
}
