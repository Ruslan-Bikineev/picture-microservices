package edu.school21.controller;

import edu.school21.dto.request.ImageRqDto;
import edu.school21.dto.response.ErrorInfoRsDto;
import edu.school21.dto.response.ImageRsDto;
import edu.school21.entity.Image;
import edu.school21.service.ImageService;
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

@Tag(name = "Image", description = "Image operations")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private final MapperUtil mapperUtil;
    private final ImageService imageService;

    @Operation(summary = "Get image by image id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImageRsDto.class))
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
    @GetMapping("/{image_id}")
    public ImageRsDto getImageById(@PathVariable(name = "image_id") Long imageId) {
        Image image = imageService.findById(imageId);
        return mapperUtil.toImageRsDto(image);
    }

    @Operation(summary = "Save image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImageRsDto.class))
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
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ImageRsDto saveImage(@Valid @RequestBody ImageRqDto imageRqDto) {
        Image image = mapperUtil.toImage(imageRqDto);
        image = imageService.save(image);
        return mapperUtil.toImageRsDto(image);
    }

    @Operation(summary = "Delete image by image id")
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
    @DeleteMapping("/{image_id}")
    public void deletedImageById(@PathVariable(name = "image_id") Long imageId) {
        imageService.setDeleted(imageId);
    }
}

