package edu.school21.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionImageId implements Serializable {
    private Long collectionId;
    private Long imageId;
}