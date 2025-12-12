package com.example.project.service;

import com.example.project.apiservice.LibrarianApiService;
import com.example.project.model.Librarian;
import java.util.List;

public class LibrarianService {
    private final LibrarianApiService librarianApiService = new LibrarianApiService();
    public List<Librarian> getApprovedLibrarians() {
        try {
            return librarianApiService.filterLibrarians(null, null, "APPROVED", null);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
