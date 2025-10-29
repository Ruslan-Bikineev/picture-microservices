package edu.school21.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRqDto {

    @Schema(description = "Comment text", example = "Example text")
    @NotBlank(message = "comment_text: не может быть пустым")
    @Size(max = 255, message = "Количество символом до 255")
    @JsonProperty("comment_text")
    private String commentText;

    @Schema(description = "User id", example = "1")
    @NotNull(message = "user_id: не может быть пустым")
    @Positive(message = "user_id: не может быть отрицательным")
    @JsonProperty("user_id")
    private Long userId;
}
