package com.example.project.controller;

import com.example.project.dto.ApiResponse;
import com.example.project.dto.request.RoleRequest;
import com.example.project.dto.response.RoleResponse;
import com.example.project.service.QrCodeService;
import com.example.project.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QRController {

    private final QrCodeService qrService;

    @GetMapping("/qr/test")
    public String testEndpoint() {
        return "QR Controller is working!";
    }

    //GET /qr?text=HELLO_SPRING
    @GetMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getQr(@RequestParam String text) {
        return qrService.generateQr(text, 300, 300);
    }
}
