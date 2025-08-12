package edu.school21.utils;

import edu.school21.dto.request.CommentRqDto;
import edu.school21.dto.response.CommentRsDto;
import edu.school21.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapperUtil {

    CommentRsDto toCommentRsDto(Comment comment);

    Comment toComment(CommentRqDto commentRqDto, Long imageId);
}
