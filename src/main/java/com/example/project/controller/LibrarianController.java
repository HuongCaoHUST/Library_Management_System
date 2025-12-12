package com.example.project.controller;

import com.example.project.dto.LibrarianResponseForFilter;
import com.example.project.service.LibrarianService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/librarians")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class LibrarianController {

    private final LibrarianService librarianService;

    public LibrarianController(LibrarianService librarianService) {
        this.librarianService = librarianService;
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

    @GetMapping("/test")
    public String testEndpoint() {
        return "LibrarianController is working!";
    }
}
