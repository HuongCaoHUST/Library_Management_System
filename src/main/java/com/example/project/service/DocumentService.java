package com.example.project.service;

import com.example.project.model.Document;
import com.example.project.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    private final DocumentRepository repository;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    public List<Document> findAll() {
        return repository.findAll();
    }

    public Optional<Document> findById(Long id) {
        return repository.findById(id);
    }

    public Document save(Document document) {
        return repository.save(document);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
