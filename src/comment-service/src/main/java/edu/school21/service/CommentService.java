package edu.school21.service;

import edu.school21.constants.CacheNames;
import edu.school21.dto.response.CommentRsDto;
import edu.school21.entity.Comment;
import edu.school21.repository.CommentRepository;
import edu.school21.specification.CommentSpecification;
import edu.school21.utils.MapperUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final MapperUtil mapperUtil;
    private final CommentRepository commentRepository;

    @Cacheable(cacheNames = CacheNames.COMMENTS_BY_IMAGE_ID, key = "#imageId")
    @Transactional(readOnly = true)
    public List<CommentRsDto> findAllByImageId(Long imageId) {
        Specification<Comment> specifications = CommentSpecification.byImageId(imageId)
                .and(CommentSpecification.isNotDeleted());
        return commentRepository.findAll(specifications).stream()
                .map(mapperUtil::toCommentRsDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Comment findByIdAndImageId(Long commentId, Long imageId) {
        Specification<Comment> specifications = CommentSpecification.byId(commentId)
                .and(CommentSpecification.byImageId(imageId))
                .and(CommentSpecification.isNotDeleted());
        return commentRepository.findOne(specifications)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id: %s and image id: %s not found"
                        .formatted(commentId, imageId)));
    }

    @CacheEvict(cacheNames = CacheNames.COMMENTS_BY_IMAGE_ID, key = "#comment.getImageId()")
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @CacheEvict(cacheNames = CacheNames.COMMENTS_BY_IMAGE_ID, key = "#imageId")
    public void setDeleted(Long imageId, Long commentId) {
        Comment comment = findByIdAndImageId(commentId, imageId);
        if (!comment.isDeleted()) {
            comment.setDeleted(true);
            commentRepository.save(comment);
        }
    }
}
