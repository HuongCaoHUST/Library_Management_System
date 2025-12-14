package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.ChangePasswordRequest;
import com.example.project.dto.request.LibrarianRequest;
import com.example.project.dto.response.LibrarianResponse;
import com.example.project.dto.response.LibrarianResponseForFilter;
import com.example.project.dto.response.UserResponse;
import com.example.project.mapper.LibrarianMapper;
import com.example.project.model.Librarian;
import com.example.project.service.LibrarianService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/librarians")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class LibrarianController {

    private final LibrarianService librarianService;
    private final PasswordEncoder passwordEncoder;

    private final LibrarianMapper mapper;

    public LibrarianController(LibrarianService librarianService, PasswordEncoder passwordEncoder, LibrarianMapper mapper) {
        this.librarianService = librarianService;
        this.passwordEncoder = passwordEncoder;
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
}
