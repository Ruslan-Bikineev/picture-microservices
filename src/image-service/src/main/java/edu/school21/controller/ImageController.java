package edu.school21.controller;

import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.request.ImageRqDto;
import edu.school21.dto.response.ImageRsDto;
import edu.school21.entity.Image;
import edu.school21.service.ImageService;
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

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private final MapperUtil mapperUtil;
    private final ImageService imageService;

    @GeneralApiResponses(summary = "Get image by image id")
    @GetMapping("/{image_id}")
    public ImageRsDto getImageById(@PathVariable(name = "image_id") Long imageId) {
        Image image = imageService.findById(imageId);
        return mapperUtil.toImageRsDto(image);
    }

    @GeneralApiResponses(summary = "Save image")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ImageRsDto saveImage(@Valid @RequestBody ImageRqDto imageRqDto) {
        Image image = mapperUtil.toImage(imageRqDto);
        image = imageService.save(image);
        return mapperUtil.toImageRsDto(image);
    }

    @GeneralApiResponses(summary = "Delete image by image id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{image_id}")
    public void deletedImageById(@PathVariable(name = "image_id") Long imageId) {
        imageService.setDeleted(imageId);
    }
}

