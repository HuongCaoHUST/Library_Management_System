package com.example.project.service;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.repository.LibrarianRepository;
import com.example.project.specification.LibrarianSpecification;
import com.example.project.specification.ReaderSpecification;
import org.springframework.data.jpa.domain.Specification;
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

    public Librarian save(Librarian librarian) {
        return repository.save(librarian);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Librarian> filterLibrarians(String fullName, String email, String status, String gender) {
        Specification<Librarian> spec = Specification
                .where(LibrarianSpecification.hasFullName(fullName))
                .and(LibrarianSpecification.hasEmail(email))
                .and(LibrarianSpecification.hasStatus(status))
                .and(LibrarianSpecification.hasGender(gender));

        return repository.findAll(spec);
    }
}
