package edu.school21.service;

import edu.school21.entity.CollectionImage;
import edu.school21.repository.CollectionImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CollectionImageService {

    private final CollectionImageRepository collectionImageRepository;

    public CollectionImage save(CollectionImage collectionImage) {
        return collectionImageRepository.save(collectionImage);
    }

    public void delete(CollectionImage collectionImage) {
        collectionImageRepository.deleteByCollectionIdAndImageId(
                collectionImage.getCollectionId(),
                collectionImage.getImageId());
    }
}