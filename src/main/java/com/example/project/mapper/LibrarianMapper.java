package com.example.project.mapper;

import com.example.project.dto.request.LibrarianRequest;
import com.example.project.dto.response.LibrarianResponse;
import com.example.project.model.Librarian;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LibrarianMapper {

    // Request -> Entity
    @Mapping(target = "userId", ignore = true)
    Librarian toEntity(LibrarianRequest request);

    // Entity -> Response
    LibrarianResponse toResponse(Librarian librarian);

    // UPDATE (PUT)
    @Mapping(target = "userId", ignore = true)
    void update(@MappingTarget Librarian entity, LibrarianRequest request);

    // PATCH
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    void patch(@MappingTarget Librarian entity, LibrarianRequest request);
}
