package edu.school21.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageRqDto {

    @Schema(description = "Image in base64", example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAQAAAAnOwc2AAAACXBIWXMAAAsTAAALEwEAmpwYAAAA82lDQ1BQaG90b3Nob3AgSUNDIHByb2ZpbGUAAHjardA/SgNBHMXx70TULloE67mAgmu15eQPi2ARYopsqkxmBw1hd4fZn3/2Bh7B43iDFIIXUVJbqARLwU/1eM2DB+rZDPujzjGUlcRsYvJZPteHGw7YAwDrmmDG4yuAqq48vynYvqEAXk/NsD/ib/ZdiAK8AEnhGwd8AKsHCQJqAfSW6yCgBOitp5MBqCeg")
    @NotBlank(message = "image_base64: не может быть пустым")
    @JsonProperty("image_base64")
    private String imageBase64;

    @Schema(description = "User id", example = "1")
    @NotNull(message = "user_id: не может быть пустым")
    @Positive(message = "user_id: не может быть отрицательным")
    @JsonProperty("user_id")
    private Long userId;
}
