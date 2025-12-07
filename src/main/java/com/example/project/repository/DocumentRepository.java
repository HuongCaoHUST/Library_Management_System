package com.example.project.repository;

import com.example.project.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    Optional<Document> findByDkcbCode(String dkcbCode);
    Optional<Document> findByTitleAndAuthorAndPublisherAndPublicationYear(
            String title, String author, String publisher, int publicationYear
    );
}
