package edu.school21.repository;

import edu.school21.entity.CollectionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionImageRepository extends JpaRepository<CollectionImage, Long> {

    @Query("SELECT EXISTS(SELECT 1 FROM collection_images ci WHERE ci.imageId = :imageId)")
    boolean isImageIdExistInCollection(Long imageId);
}