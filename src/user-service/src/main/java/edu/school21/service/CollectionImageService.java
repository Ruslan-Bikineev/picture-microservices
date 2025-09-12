package edu.school21.service;

import edu.school21.constants.CacheNames;
import edu.school21.entity.CollectionImage;
import edu.school21.repository.CollectionImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CollectionImageService {

    private final CollectionImageRepository collectionImageRepository;

    @CacheEvict(cacheNames = CacheNames.USER_COLLECTION, key = "#userId")
    public CollectionImage save(CollectionImage collectionImage, long userId) {
        return collectionImageRepository.save(collectionImage);
    }

    @CacheEvict(cacheNames = CacheNames.USER_COLLECTION, key = "#userId")
    public void delete(long collectionId, long imageId, long userId) {
        collectionImageRepository.deleteByCollectionIdAndImageId(collectionId, imageId);
    }
}