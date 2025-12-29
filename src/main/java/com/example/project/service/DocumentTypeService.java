package com.example.project.service;

import com.example.project.dto.request.CategoryRequest;
import com.example.project.dto.request.DocumentTypeRequest;
import com.example.project.dto.response.CategoryResponse;
import com.example.project.dto.response.DocumentTypeResponse;
import com.example.project.mapper.CategoryMapper;
import com.example.project.mapper.DocumentTypeMapper;
import com.example.project.model.Category;
import com.example.project.model.DocumentType;
import com.example.project.repository.CategoryRepository;
import com.example.project.repository.DocumentTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentTypeService {
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeMapper mapper;

    public List<DocumentType> findAll() {
        return documentTypeRepository.findAll();
    }

    public void delete(Long id) {
        if (!documentTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy loại tài liệu");
        }
        documentTypeRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return documentTypeRepository.existsByName(name);
    }

    @Transactional
    public DocumentTypeResponse add (DocumentTypeRequest request) {

        if (documentTypeRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Loại tài liệu đã tồn tại");
        }

        DocumentType category = mapper.toEntity(request);
        DocumentType saved = documentTypeRepository.save(category);

        return mapper.toResponse(saved);
    }
}
