package edu.school21.specification;

import edu.school21.entity.Comment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentSpecification {

    public static Specification<Comment> conjuction() {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.conjunction());
    }

    public static Specification<Comment> isNotDeleted() {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted")));
    }

    public static Specification<Comment> byImageId(Long imageId) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("imageId"), imageId));
    }

    public static Specification<Comment> byId(Long id) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), id));
    }
}
