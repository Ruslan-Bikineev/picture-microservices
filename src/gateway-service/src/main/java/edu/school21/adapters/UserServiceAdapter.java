package edu.school21.adapters;

import edu.school21.dto.request.CollectionRqDto;
import edu.school21.dto.request.UserRqDto;
import edu.school21.dto.response.CollectionRsDto;
import edu.school21.dto.response.MessageRsDto;
import edu.school21.dto.response.TokenRsDto;
import edu.school21.dto.response.UserCollectionImageRsDto;
import edu.school21.dto.response.UserRsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class UserServiceAdapter {

    private final WebClient webClient;
    private static final String USER_ID_COLLECTION_ENDPOINT = "/{user_id}/collections";

    public UserServiceAdapter(@Value("${service.url.user-service}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public MessageRsDto registerUser(UserRqDto userRqDto) {
        return webClient
                .method(HttpMethod.POST)
                .uri("/registration")
                .bodyValue(userRqDto)
                .retrieve()
                .bodyToMono(MessageRsDto.class)
                .block();
    }

    public TokenRsDto authorizeUser(UserRqDto userRqDto) {
        return webClient
                .method(HttpMethod.POST)
                .uri("/authorization")
                .bodyValue(userRqDto)
                .retrieve()
                .bodyToMono(TokenRsDto.class)
                .block();
    }

    public CollectionRsDto addedImageToUserCollection(String token, String userId, CollectionRqDto collectionRqDto) {
        return webClient
                .method(HttpMethod.POST)
                .uri(USER_ID_COLLECTION_ENDPOINT, userId)
                .bodyValue(collectionRqDto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(CollectionRsDto.class)
                .block();
    }

    public void isUserAuthorize(String token) {
        webClient
                .method(HttpMethod.GET)
                .uri("/authorization")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public UserCollectionImageRsDto getUserCollection(String token, Long userId) {
        return webClient
                .method(HttpMethod.GET)
                .uri(USER_ID_COLLECTION_ENDPOINT, userId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(UserCollectionImageRsDto.class)
                .block();
    }

    public List<UserRsDto> getUsers(String token, Integer limit, Integer offset) {
        return webClient
                .method(HttpMethod.GET)
                .uri(uriBuilder -> uriBuilder
                        .queryParam("limit", limit)
                        .queryParam("offset", offset)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserRsDto>>() {
                })
                .block();
    }

    public void deleteImageInUserCollection(String token, String userId, CollectionRqDto collectionRqDto) {
        webClient
                .method(HttpMethod.DELETE)
                .uri(USER_ID_COLLECTION_ENDPOINT, userId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(collectionRqDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
