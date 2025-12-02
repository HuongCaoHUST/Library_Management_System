package com.example.project.controller;

import com.example.project.dto.*;
import com.example.project.model.Document;
import com.example.project.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.example.project.mapper.DocumentMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {
    private final DocumentService documentService;
    private final DocumentMapper documentMapper;

    public DocumentController(DocumentService documentService, DocumentMapper documentMapper) {
        this.documentService = documentService;
        this.documentMapper = documentMapper;
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

    @PostMapping
    public ResponseEntity<DocumentResponse> createDocument(
            @Valid @RequestBody DocumentRequest request) {
        Document entity = documentMapper.toEntity(request);
        Document saved = documentService.save(entity);
        DocumentResponse response = documentMapper.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<DocumentResponse>> createBulkDocuments(
            @Valid @RequestBody DocumentBulkRequest request) {
        List<Document> entities = request.getDocuments().stream()
                .map(documentMapper::toEntity)
                .toList();
        List<Document> saved = documentService.saveAll(entities);
        List<DocumentResponse> responses = saved.stream()
                .map(documentMapper::toResponse)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable String id, @Valid @RequestBody DocumentRequest request) {  // Đổi từ Long sang String
        return documentService.findById(id)
                .map(existing -> {
                    documentMapper.updateEntityFromRequest(request, existing);
                    existing.setDocumentId(id);
                    Document updated = documentService.save(existing);
                    return ResponseEntity.ok(documentMapper.toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DocumentResponse> patchDocument(
            @PathVariable String id,  // Đổi từ Long sang String
            @Valid @RequestBody DocumentPatchRequest request) {
        return documentService.findById(id)
                .map(existing -> {
                    documentMapper.updateFromPatch(request, existing);
                    Document updated = documentService.save(existing);
                    return ResponseEntity.ok(documentMapper.toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {  // Đổi từ Long sang String
        if (!documentService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Map<String, Object>> deleteBulkDocuments(
            @Valid @RequestBody DocumentDeleteRequest request) {
        List<String> ids = request.getDocumentIds();  // Đổi từ List<Long> sang List<String>
        List<String> existingIds = documentService.findAllById(ids)
                .stream().map(Document::getDocumentId).toList();

        if (existingIds.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        documentService.deleteAllById(existingIds);

        Map<String, Object> response = new HashMap<>();
        response.put("deletedCount", existingIds.size());
        response.put("deletedIds", existingIds);
        response.put("notFoundIds", ids.stream()
                .filter(id -> !existingIds.contains(id))
                .toList());

        return ResponseEntity.ok(response);
    }
}