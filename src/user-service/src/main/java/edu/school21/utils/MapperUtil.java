package edu.school21.utils;

import edu.school21.dto.kafka.UserLogMessageDto;
import edu.school21.dto.request.CollectionRqDto;
import edu.school21.dto.request.UserRqDto;
import edu.school21.dto.response.CollectionRsDto;
import edu.school21.dto.response.MessageRsDto;
import edu.school21.dto.response.UserCollectionImageRsDto;
import edu.school21.dto.response.UserRsDto;
import edu.school21.entity.Collection;
import edu.school21.entity.CollectionImage;
import edu.school21.entity.User;
import edu.school21.entity.UserLogMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapperUtil {

    @Mapping(target = "collectionId", source = "collection.id")
    UserRsDto toUserRsDto(User user);

    User toUser(UserRqDto userRqDto);

    @Mapping(target = "collectionId", source = "collection.id")
    @Mapping(target = "imagesId", expression = "java(getImagesIdInCollection(collection))")
    UserCollectionImageRsDto toUserCollectionImageRsDto(Collection collection);

    CollectionImage toCollectionImage(CollectionRqDto collectionRqDto, Long collectionId);

    CollectionRsDto toCollectionRsDto(CollectionImage collectionImage);

    MessageRsDto toMessageRsDto(Long id, String message);

    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    UserLogMessageDto mapToUserLogMessageDto(UserRsDto source, String message);

    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    UserLogMessageDto mapToUserLogMessageDto(Long id, String username, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "id")
    UserLogMessage mapToUserLogMessage(UserLogMessageDto source);

    default List<Long> getImagesIdInCollection(Collection collection) {
        return collection.getImages().stream()
                .map(CollectionImage::getImageId)
                .toList();
    }
}
