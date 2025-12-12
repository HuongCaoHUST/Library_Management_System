package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.model.Reader;
import com.example.project.dto.ReaderResponse;
import com.example.project.service.ReaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/api/readers")
@CrossOrigin(origins = "*")
public class ReaderController {

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping("/filter")
    public List<Reader> filterReaders(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String gender
    ) {
        return readerService.filterReaders(fullName, email, status, gender);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ReaderResponse>> register(@RequestBody Reader reader) {
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
        ReaderResponse responseDTO = new ReaderResponse(savedReader);
        return ResponseEntity.ok(new ApiResponse<>(true, "Đăng ký thành công", responseDTO));
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "ReaderController is working!";
    }
}
