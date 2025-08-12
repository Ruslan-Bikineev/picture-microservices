package edu.school21.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCollectionImageRsDto {

    @JsonProperty("collection_id")
    @Schema(description = "collection id", example = "1")
    private Long collectionId;

    @JsonProperty("images_id")
    @Schema(description = "List of image IDs associated with the collection", example = "[1, 2, 3]")
    private List<Long> imagesId;
}
