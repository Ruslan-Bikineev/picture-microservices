package edu.school21.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRsDto {

    @Schema(description = "id", example = "1")
    private Long id;

    @Schema(description = "username", example = "Petr")
    private String username;

    @JsonProperty("collection_id")
    @Schema(description = "collection id", example = "1")
    private Long collectionId;
}
