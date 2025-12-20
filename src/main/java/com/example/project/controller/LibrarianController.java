package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.ChangePasswordRequest;
import com.example.project.dto.request.LibrarianRequest;
import com.example.project.dto.response.LibrarianResponse;
import com.example.project.dto.response.LibrarianResponseForFilter;
import com.example.project.dto.response.UserResponse;
import com.example.project.mapper.LibrarianMapper;
import com.example.project.model.Librarian;
import com.example.project.service.FileStorageService;
import com.example.project.service.LibrarianService;
import com.example.project.service.LibrarianService2;
import com.example.project.service.impl.LibrarianFileStorageServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/librarians")
@CrossOrigin(origins = "*")
//@PreAuthorize("hasRole('ADMIN')")
public class LibrarianController {

    private final LibrarianService librarianService;
    private final LibrarianService2 librarianService2;
    private final PasswordEncoder passwordEncoder;
    private final LibrarianMapper mapper;
    private final FileStorageService fileStorageService;

    public LibrarianController(
            LibrarianService librarianService,
            LibrarianService2 librarianService2,
            PasswordEncoder passwordEncoder,
            @Qualifier("librarianStorage") FileStorageService fileStorageService,
            LibrarianMapper mapper) {
        this.librarianService = librarianService;
        this.librarianService2 = librarianService2;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
        this.mapper = mapper;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Librarian Controller is working!";
    }

    @GetMapping("/filter")
    public ResponseEntity<List<LibrarianResponseForFilter>> filterLibrarians(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String gender
    ) {
        List<LibrarianResponseForFilter> result = librarianService.filterLibrarians(fullName, email, status, gender)
                .stream()
                .map(LibrarianResponseForFilter::new)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody Librarian librarian) {
        if (librarianService.existsByUsername(librarian.getUsername())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Username đã tồn tại", null));
        }
        if (librarianService.existsByEmail(librarian.getEmail())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Email đã tồn tại", null));
        }
        if (librarianService.existsByIdCardNumber(librarian.getIdCardNumber())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "CCCD đã tồn tại", null));
        }

        Librarian savedLibrarian = librarianService.registerLibrarian(librarian);
        UserResponse responseDTO = new UserResponse(savedLibrarian);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đăng ký thành công", responseDTO));
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String avatarUrl = fileStorageService.store(file, id);
        librarianService2.updateAvatar(id, avatarUrl);

        return ResponseEntity.ok(new ApiResponse<>(true, "Upload avatar thành công", avatarUrl));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<LibrarianResponseForFilter>> getMyLibrarianInfo(Authentication authentication) {
        String username = authentication.getName();
        Optional<Librarian> optionalLibrarian = librarianService.findByUsername(username);

        return optionalLibrarian
                .map(librarian -> {
                    LibrarianResponseForFilter responseDTO = new LibrarianResponseForFilter(librarian);
                    return ResponseEntity.ok(new ApiResponse<>(true, "Librarian found", responseDTO));
                })
                .orElseGet(() -> ResponseEntity.ok(new ApiResponse<>(false, "Librarian not found", null)));
    }

    @PostMapping("/me/change-password")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> changeMyPassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request) {
        String username = authentication.getName();
        Optional<Librarian> optionalLibrarian = librarianService.findByUsername(username);
        if (optionalLibrarian.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Librarian not found", null));
        }
        Librarian librarian = optionalLibrarian.get();
        if (!passwordEncoder.matches(request.getOldPassword(), librarian.getPassword())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Old password is incorrect", null));
        }
        librarian.setPassword(passwordEncoder.encode(request.getNewPassword()));
        librarianService.save(librarian);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<LibrarianResponse>> patchLibrarian(
            @PathVariable Long id,
            @Valid @RequestBody LibrarianRequest request) {
        Librarian updated = librarianService.updatePatch(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật thành công!", mapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLibrarian(@PathVariable Long id) {

        librarianService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa librarian thành công!", null));
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportReaderList() {

        ByteArrayInputStream excelStream = librarianService.exportLibrarianListToExcel();

        InputStreamResource resource = new InputStreamResource(excelStream);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=librarian_list_export.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(resource);
    }
}
