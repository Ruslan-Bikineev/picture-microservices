package edu.school21.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
@IdClass(CollectionImageId.class)
@Entity(name = "collection_images")
public class CollectionImage implements Serializable {
    @Id
    @Column(name = "collection_id")
    private Long collectionId;

    @Id
    @Column(name = "image_id")
    private Long imageId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", insertable = false, updatable = false)
    private Collection collection;
}
