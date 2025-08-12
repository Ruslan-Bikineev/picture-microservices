package edu.school21.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.school21.adapters.ImageServiceAdapter;
import edu.school21.adapters.UserServiceAdapter;
import edu.school21.dto.request.CollectionRqDto;
import edu.school21.dto.request.ImageRqDto;
import edu.school21.dto.response.CollectionRsDto;
import edu.school21.dto.response.ErrorInfoRsDto;
import edu.school21.dto.response.ImageRsDto;
import edu.school21.dto.response.UserCollectionImageRsDto;
import edu.school21.dto.response.UserRsDto;
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

@Tag(name = "User", description = "User operations")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final JwtUtil jwtUtil;
    private final MapperUtil mapperUtil;
    private final UserServiceAdapter userServiceAdapter;
    private final ImageServiceAdapter imageServiceAdapter;

    @Operation(summary = "Save image to collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CollectionRsDto.class))
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

    @Operation(summary = "Get user collection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserCollectionImageRsDto.class))
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
    @GetMapping("/{user_id}/collections")
    public UserCollectionImageRsDto getUserCollection(
            HttpServletRequest request,
            @PathVariable(name = "user_id") Long userId) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        return userServiceAdapter.getUserCollection(token, userId);
    }

    @Operation(summary = "Get users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = UserRsDto.class))
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

    @Operation(summary = "Delete image from user collection")
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
