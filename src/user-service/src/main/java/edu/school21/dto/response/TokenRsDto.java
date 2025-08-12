package edu.school21.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRsDto {

    @JsonProperty("access_token")
    @Schema(description = "Access token", example = "Jwt token")
    private String accessToken;
}
