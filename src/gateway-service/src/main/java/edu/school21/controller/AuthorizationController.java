package edu.school21.controller;

import edu.school21.adapters.UserServiceAdapter;
import edu.school21.dto.request.UserRqDto;
import edu.school21.dto.response.ErrorInfoRsDto;
import edu.school21.dto.response.MessageRsDto;
import edu.school21.dto.response.TokenRsDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authorization", description = "Authorization operations")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
public class AuthorizationController {

    private final UserServiceAdapter userServiceAdapter;

    @Operation(summary = "Authorization user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenRsDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    })
    })
    @PostMapping("/authorization")
    public TokenRsDto authorizeUser(@Valid @RequestBody UserRqDto userRqDto) {
        return userServiceAdapter.authorizeUser(userRqDto);
    }

    @Operation(summary = "Registration user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageRsDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoRsDto.class))
                    })
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    public MessageRsDto registerUser(@Valid @RequestBody UserRqDto userRqDto) {
        return userServiceAdapter.registerUser(userRqDto);
    }
}
