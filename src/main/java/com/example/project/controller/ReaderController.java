package com.example.project.controller;

import com.example.project.model.Reader;
import com.example.project.service.ReaderService;
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

    @GetMapping("/test")
    public String testEndpoint() {
        return "ReaderController is working!";
    }
}
