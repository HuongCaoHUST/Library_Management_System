package com.example.project.repository;

import com.example.project.model.Category;
import com.example.project.model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long>, JpaSpecificationExecutor<DocumentType> {
    Optional<DocumentType> findByName (String name);
    boolean existsByName(String name);
}