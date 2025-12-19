package com.example.project.controller;

import com.example.project.dto.*;
import com.example.project.dto.request.DocumentBulkRequest;
import com.example.project.dto.request.DocumentDeleteRequest;
import com.example.project.dto.request.DocumentPatchRequest;
import com.example.project.dto.request.DocumentRequest;
import com.example.project.dto.response.DocumentResponse;
import com.example.project.dto.response.DocumentResponseForAdd;
import com.example.project.model.Document;
import com.example.project.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import com.example.project.mapper.DocumentMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentMapper documentMapper;

    @GetMapping("/filter")
    public List<DocumentResponse> filterDocuments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) Long documentType,
            @RequestParam(required = false) Integer publicationYear
    ) {
        return documentService.filterDocuments(title, author, publisher, documentType, publicationYear).stream()
                .map(documentMapper::toResponse)
                .toList();
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "DocumentsController is working!";
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<DocumentResponseForAdd>> addDocument(
            @Valid @RequestBody DocumentRequest request) {

        Document saved = documentService.create(request);

        DocumentResponseForAdd responseDTO = new DocumentResponseForAdd(saved);
        return ResponseEntity.ok(new ApiResponse<>(true, "Thêm tài liệu thành công", responseDTO));
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

    @GetMapping("/import_template")
    public ResponseEntity<Resource> downloadImportTemplate() {

        InputStreamResource resource = new InputStreamResource(
                getClass().getResourceAsStream("/templates/document_import_template.xlsx")
        );

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=document_import_template.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(resource);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importSuppliers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File upload rỗng");
        }

        String contentType = file.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
            return ResponseEntity.badRequest().body("Chỉ hỗ trợ file .xlsx");
        }

        try {
            InputStream inputStream = file.getInputStream();
            System.out.println("Upload thành công");
//             TODO: xử lý đọc Excel (Apache POI)
//             importService.importSupplierFromExcel(inputStream);

            return ResponseEntity.ok("Upload và import thành công");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi khi xử lý file: " + e.getMessage());
        }
    }
}
