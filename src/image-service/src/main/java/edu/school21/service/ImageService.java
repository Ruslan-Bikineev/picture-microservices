package edu.school21.service;

import edu.school21.entity.Image;
import edu.school21.repository.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public Image findById(Long id) {
        return imageRepository.findById(id)
                .filter(image -> !image.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Image with id: %s not found".formatted(id)));
    }

    public Image save(Image image) {
        return imageRepository.save(image);
    }

    public void setDeleted(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image with id: %s not found".formatted(id)));
        if (!image.isDeleted()) {
            image.setDeleted(true);
            imageRepository.save(image);
        }
    }
}
