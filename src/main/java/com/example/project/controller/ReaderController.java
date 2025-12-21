package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.ChangePasswordRequest;
import com.example.project.dto.request.ReaderRequest;
import com.example.project.dto.response.*;
import com.example.project.mapper.ReaderMapper;
import com.example.project.model.Reader;
import com.example.project.service.*;
import com.example.project.service.impl.ReaderFileStorageServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/readers")
@CrossOrigin(origins = "*")
public class ReaderController {

    private final ReaderService readerService;
    private final ReaderService2 readerService2;
    private final PasswordEncoder passwordEncoder;
    private final ReaderMapper mapper;
    private final FileStorageService fileStorageService;
    private final ReaderCardPdfService pdfService;
    private final ExportService exportService;

    public ReaderController(
            ReaderService readerService,
            ReaderService2 readerService2,
            PasswordEncoder passwordEncoder,
            ReaderMapper mapper,
            @Qualifier("readerStorage") FileStorageService fileStorageService, ReaderCardPdfService pdfService, ExportService exportService) {
        this.readerService = readerService;
        this.readerService2 = readerService2;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
        this.mapper = mapper;
        this.pdfService = pdfService;
        this.exportService = exportService;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "ReaderController is working!";
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ReaderResponseForFilter>> filterReaders(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String gender
    ) {
        List<ReaderResponseForFilter> result = readerService.filterReaders(fullName, email, status, gender)
                .stream()
                .map(ReaderResponseForFilter::new)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ReaderResponse>> getMyReaderInfo(Authentication authentication) {
        String username = authentication.getName();
        Optional<Reader> optionalReader = readerService.findByUsername(username);

        return optionalReader.map(reader -> {
                    ReaderResponse responseDTO = new ReaderResponse(reader);
                    return ResponseEntity.ok(new ApiResponse<>(true, "Reader found", responseDTO));
                })
                .orElseGet(() -> ResponseEntity.ok(new ApiResponse<>(false, "Reader not found", null)));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<ApiResponse<Void>> changeMyPassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request) {
        String username = authentication.getName();
        Optional<Reader> optionalLibrarian = readerService.findByUsername(username);
        if (optionalLibrarian.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Librarian not found", null));
        }
        Reader reader = optionalLibrarian.get();
        if (!passwordEncoder.matches(request.getOldPassword(), reader.getPassword())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Old password is incorrect", null));
        }
        reader.setPassword(passwordEncoder.encode(request.getNewPassword()));
        readerService.save(reader);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody ReaderRequest request) {

        try {
            UserResponse response = readerService.registerReader(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm reader thành công", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String avatarUrl = fileStorageService.store(file, id);
        readerService2.updateAvatar(id, avatarUrl);

        return ResponseEntity.ok(new ApiResponse<>(true, "Upload avatar thành công", avatarUrl));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> patchLibrarian(
            @PathVariable Long id,
            @Valid @RequestBody ReaderRequest request) {
        Reader updated = readerService.updatePatch(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật thành công!", mapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLibrarian(@PathVariable Long id) {

        readerService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa librarian thành công!", null));
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportReaderList() {

        ByteArrayInputStream excelStream = exportService.exportReaderListToExcel();

        InputStreamResource resource = new InputStreamResource(excelStream);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reader_list_export.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(resource);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importReaders(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(false, "File upload rỗng", ""));
        }

        String contentType = file.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Chỉ hỗ trợ file .xlsx", ""));
        }

        try {
            InputStream inputStream = file.getInputStream();
            readerService.importReaderFromExcel(inputStream);

            return ResponseEntity.ok(new ApiResponse<>(true, "Upload thành công", ""));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Lỗi khi xử lý file: "+ e.getMessage(), ""));
        }
    }

    @GetMapping("/{id}/card-pdf")
    public ResponseEntity<byte[]> exportReaderCard(@PathVariable Long id) {

        Reader reader = readerService.findById(id);

        byte[] pdf = pdfService.exportReaderCard(reader);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reader-card-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
