package edu.school21.service;

import edu.school21.constants.CacheNames;
import edu.school21.dto.response.CommentRsDto;
import edu.school21.entity.Comment;
import edu.school21.repository.CommentRepository;
import edu.school21.utils.MapperUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final MapperUtil mapperUtil;
    private final CommentRepository commentRepository;
    private final CollectionImageService collectionImageService;

    @Cacheable(cacheNames = CacheNames.COMMENTS_BY_IMAGE_ID, key = "#imageId")
    public List<CommentRsDto> findAllCommentsByImageId(Long imageId) {
        return commentRepository.findByImageId(imageId).stream()
                .filter(comment -> !comment.isDeleted())
                .map(mapperUtil::toCommentRsDto)
                .collect(Collectors.toList());
    }

    public Comment findCommentByImageIdAndUserId(Long imageId, Long commentId) {
        return commentRepository.findById(commentId)
                .filter(comment -> comment.getImageId().equals(imageId))
                .filter(comment -> !comment.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Comment with id: %s and image id: %s not found".formatted(commentId, imageId)));
    }

    @CacheEvict(cacheNames = CacheNames.COMMENTS_BY_IMAGE_ID, key = "#comment.getImageId()")
    public Comment save(Comment comment) {
        if (collectionImageService.isImageIdExists(comment.getImageId())) {
            return commentRepository.save(comment);
        } else {
            throw new EntityNotFoundException("Image with id: %s not found".formatted(comment.getImageId()));
        }
    }

    @CacheEvict(cacheNames = CacheNames.COMMENTS_BY_IMAGE_ID, key = "#imageId")
    public void setDeleted(Long imageId, Long commentId) {
        Comment comment = findCommentByImageIdAndUserId(imageId, commentId);
        if (!comment.isDeleted()) {
            comment.setDeleted(true);
            commentRepository.save(comment);
        }
    }
}
