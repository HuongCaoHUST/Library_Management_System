package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.ChangePasswordRequest;
import com.example.project.dto.request.ReaderRequest;
import com.example.project.dto.response.*;
import com.example.project.mapper.ReaderMapper;
import com.example.project.model.Reader;
import com.example.project.service.ReaderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/readers")
@CrossOrigin(origins = "*")
public class ReaderController {

    private final ReaderService readerService;
    private final PasswordEncoder passwordEncoder;
    private final ReaderMapper mapper;

    public ReaderController(ReaderService readerService, PasswordEncoder passwordEncoder, ReaderMapper mapper) {
        this.readerService = readerService;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
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
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody Reader reader) {
        if (readerService.existsByUsername(reader.getUsername())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Username đã tồn tại", null));
        }
        if (readerService.existsByEmail(reader.getEmail())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Email đã tồn tại", null));
        }
        if (readerService.existsByIdCardNumber(reader.getIdCardNumber())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "CCCD đã tồn tại", null));
        }

        Reader savedReader = readerService.registerReader(reader);
        UserResponse responseDTO = new UserResponse(savedReader);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đăng ký thành công", responseDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ReaderResponse>> patchLibrarian(
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
}
