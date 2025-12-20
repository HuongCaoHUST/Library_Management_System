package com.example.project.service.impl;

import com.example.project.model.Document;
import com.example.project.repository.DocumentRepository;
import com.example.project.service.DocumentService2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService2 {

    private final DocumentRepository documentRepository;

    @Override
    public void updateCover(Long documentId, String avatarUrl) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document không tồn tại"));
        document.setCoverUrl(avatarUrl);
        documentRepository.save(document);
    }
}
