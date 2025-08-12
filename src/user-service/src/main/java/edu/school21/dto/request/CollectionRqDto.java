package edu.school21.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionRqDto {

    @Schema(description = "Image ID associated with the collection", example = "1")
    @NotNull(message = "image_id: не может быть пустым")
    @Positive(message = "image_id: не может быть отрицательным")
    @JsonProperty("image_id")
    private Long imageId;
}