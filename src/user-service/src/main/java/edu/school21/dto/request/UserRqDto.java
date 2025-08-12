package edu.school21.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRqDto {

    @NotBlank(message = "username: cannot be empty")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    @Schema(description = "Client name", example = "John")
    private String username;

    @NotBlank(message = "password: cannot be empty")
    @Size(min = 4, max = 255, message = "Password must be between 4 and 255 characters")
    @Schema(description = "Client password", example = "Pass")
    private String password;
}
