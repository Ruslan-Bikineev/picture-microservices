package edu.school21.controller;

import edu.school21.adapters.UserServiceAdapter;
import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.request.UserRqDto;
import edu.school21.dto.response.MessageRsDto;
import edu.school21.dto.response.TokenRsDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
public class AuthorizationController {

    private final UserServiceAdapter userServiceAdapter;

    @GeneralApiResponses(summary = "Authorization user")
    @PostMapping("/authorization")
    public TokenRsDto authorizeUser(@Valid @RequestBody UserRqDto userRqDto) {
        return userServiceAdapter.authorizeUser(userRqDto);
    }

    @GeneralApiResponses(summary = "Registration user")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    public MessageRsDto registerUser(@Valid @RequestBody UserRqDto userRqDto) {
        return userServiceAdapter.registerUser(userRqDto);
    }
}
