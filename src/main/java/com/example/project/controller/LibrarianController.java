package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.ChangePasswordRequest;
import com.example.project.dto.LibrarianResponseForFilter;
import com.example.project.dto.UserResponse;
import com.example.project.model.Librarian;
import com.example.project.service.LibrarianService;
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

    public LibrarianController(LibrarianService librarianService, PasswordEncoder passwordEncoder) {
        this.librarianService = librarianService;
        this.passwordEncoder = passwordEncoder;
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


    @GetMapping("/test")
    public String testEndpoint() {
        return "LibrarianController is working!";
    }
}
