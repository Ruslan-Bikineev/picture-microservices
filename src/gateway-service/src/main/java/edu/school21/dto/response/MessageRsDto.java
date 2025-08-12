package edu.school21.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRsDto {

    @Schema(description = "id", example = "1")
    private Long id;

    @Schema(description = "Message", example = "User: %s successfully registered")
    private String message;
}
