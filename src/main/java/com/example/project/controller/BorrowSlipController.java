package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.BorrowSlipRequest;
import com.example.project.dto.response.BorrowSlipResponse;
import com.example.project.mapper.BorrowSlipMapper;
import com.example.project.model.BorrowSlip;
import com.example.project.service.BorrowSlipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow_slips")
public class BorrowSlipController {

    private final BorrowSlipService borrowSlipService;

    private BorrowSlipMapper mapper;

    public BorrowSlipController(BorrowSlipService borrowSlipService, BorrowSlipMapper mapper) {
        this.borrowSlipService = borrowSlipService;
        this.mapper = mapper;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Borrow Slip Controller is working!";
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<BorrowSlipResponse>> create(@RequestBody BorrowSlipRequest request) {
        try {
            BorrowSlip borrowSlip = borrowSlipService.create(request);
            BorrowSlipResponse response = mapper.toResponse(borrowSlip);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm phiếu mượn thành công", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.ok(new ApiResponse<>(false, ex.getMessage(), null));
        }
    }
}
