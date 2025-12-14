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

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<DocumentResponseForAdd>> createDocument(
            @Valid @RequestBody DocumentRequest request) {

        Document entity = documentMapper.toEntity(request);
        Document saved = documentService.save(entity);

        DocumentResponseForAdd responseDTO = new DocumentResponseForAdd(saved);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đăng ký thành công", responseDTO));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<DocumentResponse>> createBulkDocuments(
            @Valid @RequestBody DocumentBulkRequest request) {

        List<Document> entities = request.getDocuments().stream()
                .map(documentMapper::toEntity)
                .toList();

        List<Document> saved = documentService.saveAll(entities); // Lưu nhiều

        List<DocumentResponse> responses = saved.stream()
                .map(documentMapper::toResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    // Put one document
    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable Long id, @Valid @RequestBody DocumentRequest request) {

        return documentService.findById(id)
                .map(existing -> {
                    documentMapper.updateEntityFromRequest(request, existing);
                    existing.setDocumentId(id); // giữ ID
                    Document updated = documentService.save(existing);
                    return ResponseEntity.ok(documentMapper.toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DocumentResponse> patchDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentPatchRequest request) {

        return documentService.findById(id)
                .map(existing -> {
                    documentMapper.updateFromPatch(request, existing);
                    Document updated = documentService.save(existing);
                    return ResponseEntity.ok(documentMapper.toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete 1 document
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        if (!documentService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        documentService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Map<String, Object>> deleteBulkDocuments(
            @Valid @RequestBody DocumentDeleteRequest request) {

        List<Long> ids = request.getDocumentIds();
        List<Long> existingIds = documentService.findAllById(ids)
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
