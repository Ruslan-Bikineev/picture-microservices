package edu.school21.service;

import edu.school21.entity.Comment;
import edu.school21.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CollectionImageService collectionImageService;

    public List<Comment> findAllCommentsByImageId(Long imageId) {
        return commentRepository.findByImageId(imageId).stream()
                .filter(comment -> !comment.isDeleted())
                .toList();
    }

    public Comment findCommentsByImageIdAndUserId(Long imageId, Long commentId) {
        return commentRepository.findById(commentId)
                .filter(comment -> comment.getImageId().equals(imageId))
                .filter(comment -> !comment.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Comment with id: %s and image id: %s not found".formatted(commentId, imageId)));
    }

    public Comment save(Comment comment) {
        if (collectionImageService.isImageIdExists(comment.getImageId())) {
            return commentRepository.save(comment);
        } else {
            throw new EntityNotFoundException("Image with id: %s not found".formatted(comment.getImageId()));
        }
    }

    public void setDeleted(Long imageId, Long commentId) {
        Comment comment = findCommentsByImageIdAndUserId(imageId, commentId);
        if (!comment.isDeleted()) {
            comment.setDeleted(true);
            commentRepository.save(comment);
        }
    }
}
