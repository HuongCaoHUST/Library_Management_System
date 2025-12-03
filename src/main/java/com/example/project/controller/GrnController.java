package com.example.project.controller;

import com.example.project.model.Grn;
import com.example.project.service.GrnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grns")
@CrossOrigin(origins = "*")
public class GrnController {

    private final GrnService grnService;

    @Autowired
    public GrnController(GrnService grnService) {
        this.grnService = grnService;
    }

    /**
     * GET /api/grns - Lấy tất cả phiếu nhập kho
     */
    @GetMapping
    public ResponseEntity<List<Grn>> getAllGrns() {
        return ResponseEntity.ok(grnService.findAll());
    }

    /**
     * GET /api/grns/{receiptId} - Lấy phiếu nhập kho theo receiptId
     */
    @GetMapping("/{receiptId}")
    public ResponseEntity<Grn> getGrnByReceiptId(@PathVariable String receiptId) {
        return grnService.findByReceiptId(receiptId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/grns - Tạo phiếu nhập kho mới
     */
    @PostMapping
    public ResponseEntity<Grn> createGrn(@RequestBody Grn grn) {
        try {
            Grn savedGrn = grnService.saveGrn(grn);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGrn);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/grns/{receiptId} - Xoá phiếu nhập kho theo receiptId
     */
    @DeleteMapping("/{receiptId}")
    public ResponseEntity<Void> deleteGrn(@PathVariable String receiptId) {
        if (!grnService.findByReceiptId(receiptId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        grnService.deleteByReceiptId(receiptId);
        return ResponseEntity.noContent().build();
    }
}