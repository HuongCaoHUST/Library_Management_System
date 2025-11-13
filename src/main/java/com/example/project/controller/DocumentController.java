package com.example.project.controller;

import com.example.project.model.Document;
import com.example.project.service.DocumentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/filter")
    public List<Document> filterReaders(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) Integer publicationYear
    ) {
        return documentService.filterDocuments(title, author, publisher, documentType, publicationYear);
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "DocumentsController is working!";
    }
}
