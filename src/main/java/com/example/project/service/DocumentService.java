package com.example.project.service;

import com.example.project.model.Document;
import com.example.project.repository.DocumentRepository;
import com.example.project.specification.DocumentSpecification;
import org.springframework.data.jpa.domain.Specification;
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

    public Optional<Document> findById(String id) {  // Đổi từ Long sang String
        return repository.findById(id);
    }

    public Document save(Document document) {
        return repository.save(document);
    }

    public void delete(String id) {  // Đổi từ Long sang String
        repository.deleteById(id);
    }

    public List<Document> filterDocuments(String title, String author, String publisher,
                                          String documentType, Integer publicationYear) {
        Specification<Document> spec = Specification
                .where(DocumentSpecification.hasTitle(title))
                .and(DocumentSpecification.hasAuthor(author))
                .and(DocumentSpecification.hasPublisher(publisher))
                .and(DocumentSpecification.hasDocumentType(documentType))
                .and(DocumentSpecification.hasPublicationYear(publicationYear));
        return repository.findAll(spec);
    }

    public List<Document> findAllById(List<String> ids) {  // Đổi từ List<Long> sang List<String>
        return repository.findAllById(ids);
    }

    public List<Document> saveAll(List<Document> documents) {
        return repository.saveAll(documents);
    }

    public void deleteAllById(List<String> ids) {  // Đổi từ List<Long> sang List<String>
        repository.deleteAllById(ids);
    }
}