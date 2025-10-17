package edu.school21.controller;

import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.request.CollectionRqDto;
import edu.school21.dto.request.UserRqDto;
import edu.school21.dto.response.CollectionRsDto;
import edu.school21.dto.response.MessageRsDto;
import edu.school21.dto.response.TokenRsDto;
import edu.school21.dto.response.UserCollectionImageRsDto;
import edu.school21.dto.response.UserRsDto;
import edu.school21.entity.Collection;
import edu.school21.entity.CollectionImage;
import edu.school21.entity.User;
import edu.school21.service.CollectionImageService;
import edu.school21.service.UserService;
import edu.school21.utils.JwtUtil;
import edu.school21.utils.MapperUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final JwtUtil jwtUtils;
    private final MapperUtil mapperUtil;
    private final UserService userService;
    private final CollectionImageService collectionImageService;
    private final AuthenticationManager authenticationManager;

    @GeneralApiResponses(summary = "Get all user collection by user id")
    @GetMapping("/{user_id}/collections")
    public UserCollectionImageRsDto getUserCollections(@PathVariable("user_id") Long userId) {
        return userService.findUserCollection(userId);
    }

    @GeneralApiResponses(summary = "Get users with pagination")
    @GetMapping
    public List<UserRsDto> getUsers(
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "must be greater than 0")
            @Max(value = 20, message = "must be less than 21")
            Integer limit,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "must be positive")
            Integer offset) {
        return userService.findAll(PageRequest.of(offset, limit));
    }

    @GeneralApiResponses(summary = "Check authorization user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/authorization")
    public void isUserAuthorize(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String jwt = jwtUtils.extractToken(token);
        jwtUtils.validateJwtToken(jwt);
    }

    @GeneralApiResponses(summary = "Registration user")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    public MessageRsDto registerUser(@Valid @RequestBody UserRqDto userRqDto) {
        User user = mapperUtil.toUser(userRqDto);
        user.setCollection(new Collection());
        UserRsDto userRsDto = userService.save(user);
        return mapperUtil.toMessageRsDto(userRsDto.getId(), "Пользователь: %s успешно зарегистрирован".formatted(userRsDto.getUsername()));
    }

    @GeneralApiResponses(summary = "Authorization user")
    @PostMapping("/authorization")
    public TokenRsDto authorizeUser(@Valid @RequestBody UserRqDto userRqDto) {
        User user = mapperUtil.toUser(userRqDto);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        long userId = userService.findByUsername(userDetails.getUsername()).getId();
        String jwt = jwtUtils.generateToken(userId);
        return new TokenRsDto(jwt);
    }

    @GeneralApiResponses(summary = "Added image in user collection")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{user_id}/collections")
    public CollectionRsDto postImageToUserCollection(
            @Valid @RequestBody CollectionRqDto collectionRqDto,
            @PathVariable("user_id") Long userId) {
        UserCollectionImageRsDto userCollection = userService.findUserCollection(userId);
        userService.findUserCollection(userId);
        boolean isExist = userCollection.getImagesId().stream()
                .anyMatch(imageId -> Objects.equals(imageId, collectionRqDto.getImageId()));
        if (isExist) {
            throw new EntityExistsException("Указанный imageId: %s уже существует в коллекции пользователя"
                    .formatted(collectionRqDto.getImageId()));
        } else {
            CollectionImage collectionImage = mapperUtil.toCollectionImage(collectionRqDto, userCollection.getCollectionId());
            collectionImage = collectionImageService.save(collectionImage, userId);
            return mapperUtil.toCollectionRsDto(collectionImage);
        }
    }

    @GeneralApiResponses(summary = "Delete image in user collection")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{user_id}/collections")
    public void deleteImageInUserCollection(
            @Valid @RequestBody CollectionRqDto collectionRqDto,
            @PathVariable("user_id") Long userId) {
        UserCollectionImageRsDto userCollection = userService.findUserCollection(userId);
        long existImageId = userCollection.getImagesId().stream()
                .filter(imageId -> Objects.equals(imageId, collectionRqDto.getImageId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Изображение с ID " + collectionRqDto.getImageId() + " не найдено в коллекции пользователя c ID " + userId));
        collectionImageService.delete(userCollection.getCollectionId(), existImageId, userId);
    }
}