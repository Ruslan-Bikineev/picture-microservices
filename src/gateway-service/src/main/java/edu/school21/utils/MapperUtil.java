package edu.school21.utils;

import edu.school21.dto.request.CollectionRqDto;
import edu.school21.dto.request.CommentRqDto;
import edu.school21.dto.request.ImageRqDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapperUtil {

    ImageRqDto toImageRqDto(String userId, String imageBase64);

    CollectionRqDto toCollectionRqDto(Long imageId);

    CommentRqDto toCommentRqDto(String userId, String commentText);
}
