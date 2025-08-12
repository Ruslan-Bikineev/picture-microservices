package edu.school21.service;

import edu.school21.repository.CollectionImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CollectionImageService {

    private final CollectionImageRepository collectionImageRepository;

    public boolean isImageIdExists(Long imageId) {
        return collectionImageRepository.isImageIdExistInCollection(imageId);
    }
}