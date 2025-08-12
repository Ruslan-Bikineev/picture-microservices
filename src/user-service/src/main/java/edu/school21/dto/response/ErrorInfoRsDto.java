package edu.school21.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfoRsDto {

    @Schema(description = "Request path", example = "{url}/api/v1/{api}")
    private String path;

    @Schema(description = "Error message", example = "Incorrect data")
    private String message;

    @Schema(description = "Error code", example = "400")
    private Integer code;
}
