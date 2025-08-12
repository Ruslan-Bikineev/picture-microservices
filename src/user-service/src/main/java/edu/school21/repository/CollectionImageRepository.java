package edu.school21.repository;

import edu.school21.entity.CollectionImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionImageRepository extends JpaRepository<CollectionImage, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM collection_images ci WHERE ci.collectionId = :collectionId AND ci.imageId = :imageId")
    void deleteByCollectionIdAndImageId(Long collectionId, Long imageId);
}