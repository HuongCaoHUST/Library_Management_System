package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.LibrarianRequest;
import com.example.project.dto.request.ReaderRequest;
import com.example.project.dto.response.LibrarianResponse;
import com.example.project.dto.response.ReaderResponse;
import com.example.project.dto.response.ReaderResponseForFilter;
import com.example.project.mapper.LibrarianMapper;
import com.example.project.mapper.ReaderMapper;
import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.dto.response.UserResponse;
import com.example.project.service.ReaderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
@CrossOrigin(origins = "*")
public class ReaderController {

    private final ReaderService readerService;
    private final ReaderMapper mapper;

    public ReaderController(ReaderService readerService, ReaderMapper mapper) {
        this.readerService = readerService;
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
