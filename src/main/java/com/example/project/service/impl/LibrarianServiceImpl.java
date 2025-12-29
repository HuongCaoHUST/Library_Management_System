package com.example.project.service.impl;

import com.example.project.model.Librarian;
import com.example.project.repository.LibrarianRepository;
import com.example.project.service.LibrarianService2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibrarianServiceImpl implements LibrarianService2 {

    private final LibrarianRepository librarianRepository;

    @Override
    public void updateAvatar(Long librarianId, String avatarUrl) {
        Librarian librarian = librarianRepository.findById(librarianId)
                .orElseThrow(() -> new RuntimeException("Librarian không tồn tại"));
        librarian.setAvatarUrl(avatarUrl);
        librarianRepository.save(librarian);
    }
}
