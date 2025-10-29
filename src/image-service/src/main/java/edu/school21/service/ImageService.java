package edu.school21.service;

import edu.school21.entity.Image;
import edu.school21.repository.ImageRepository;
import edu.school21.specification.ImageSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional(readOnly = true)
    public Image findById(Long id) {
        Specification<Image> specifications = ImageSpecification.byId(id)
                        .and(ImageSpecification.isNotDeleted());
        return imageRepository.findOne(specifications)
                .orElseThrow(() -> new EntityNotFoundException("Image with id: %s not found".formatted(id)));
    }

    public Image save(Image image) {
        return imageRepository.save(image);
    }

    public void setDeleted(Long id) {
        Image image = findById(id);
        if (!image.isDeleted()) {
            image.setDeleted(true);
            imageRepository.save(image);
        }
    }
}
