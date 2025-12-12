package com.example.project.repository;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibrarianRepository extends JpaRepository<Librarian, Long>, JpaSpecificationExecutor<Librarian> {
    Optional<Librarian> findByUsernameAndPassword(String username, String password);
    Optional<Librarian> findByEmailAndPassword(String email, String password);
    Optional<Librarian> findById(Long id);
}
