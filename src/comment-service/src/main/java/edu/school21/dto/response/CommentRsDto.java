package edu.school21.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRsDto implements Serializable {

    @Schema(description = "Comment id", example = "1")
    private Long id;

    @JsonProperty("image_id")
    @Schema(description = "Image id", example = "1")
    private Long imageId;

    @JsonProperty("user_id")
    @Schema(description = "User id", example = "1")
    private Long userId;

    @JsonProperty("comment_text")
    @Schema(description = "Comment text", example = "Example comment")
    private String commentText;
}
