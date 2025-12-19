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

    public Optional<Document> findById(Long id) {
        return repository.findById(id);
    }

    public Document save(Document document) {
        return repository.save(document);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Document> filterDocuments(String title, String author, String publisher, Long documentTypeId, Integer publicationYear) {
        Specification<Document> spec = Specification
                .where(DocumentSpecification.hasTitle(title))
                .and(DocumentSpecification.hasAuthor(author))
                .and(DocumentSpecification.hasPublisher(publisher))
                .and(DocumentSpecification.hasDocumentTypeId(documentTypeId))
                .and(DocumentSpecification.hasPublicationYear(publicationYear));

        return repository.findAll(spec);
    }

    public List<Document> findAllById(List<Long> ids) {
        return repository.findAllById(ids);
    }

    public List<Document> saveAll(List<Document> documents) {
        return repository.saveAll(documents);
    }

    public void deleteAllById(List<Long> ids) {
        repository.deleteAllById(ids);
    }
}
