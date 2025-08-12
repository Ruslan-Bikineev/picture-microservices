package edu.school21.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionRsDto {

    @JsonProperty("collection_id")
    @Schema(description = "Collection id", example = "1")
    private Long collectionId;

    @JsonProperty("image_id")
    @Schema(description = "Image ID associated with the collection", example = "1")
    private Long imageId;
}
