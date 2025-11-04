package com.example.project.service;

import com.example.project.model.Librarian;
import com.example.project.repository.LibrarianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibrarianService {
    private final LibrarianRepository repository;

    public LibrarianService(LibrarianRepository repository) {
        this.repository = repository;
    }

    public List<Librarian> findAll() {
        return repository.findAll();
    }

    public Optional<Librarian> findById(Long id) {
        return repository.findById(id);
    }

    public Librarian save(Librarian reader) {
        return repository.save(reader);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
