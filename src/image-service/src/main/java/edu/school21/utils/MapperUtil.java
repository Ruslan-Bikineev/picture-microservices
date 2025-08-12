package edu.school21.utils;

import edu.school21.dto.request.ImageRqDto;
import edu.school21.dto.response.ImageRsDto;
import edu.school21.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapperUtil {

    ImageRsDto toImageRsDto(Image image);

    Image toImage(ImageRqDto imageRqDto);
}
