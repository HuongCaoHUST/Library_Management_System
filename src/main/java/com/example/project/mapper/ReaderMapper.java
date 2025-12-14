package com.example.project.mapper;

import com.example.project.dto.request.ReaderRequest;
import com.example.project.dto.response.ReaderResponse;
import com.example.project.model.Reader;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReaderMapper {

    // Request -> Entity
    @Mapping(target = "userId", ignore = true)
    Reader toEntity(ReaderRequest request);

    // Entity -> Response
    ReaderResponse toResponse(Reader reader);

    // UPDATE (PUT)
    @Mapping(target = "userId", ignore = true)
    void update(@MappingTarget Reader entity, ReaderRequest request);

    // PATCH
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    void patch(@MappingTarget Reader entity, ReaderRequest request);
}
