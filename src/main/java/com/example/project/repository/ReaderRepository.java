package com.example.project.repository;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long>, JpaSpecificationExecutor<Reader> {
    Optional<Reader> findByUsernameAndPassword(String username, String password);
    Optional<Reader> findByEmailAndPassword(String email, String password);
    List<Reader> findByStatus(String status);
    boolean existsByEmail(String email);
    boolean existsByIdCardNumber(String idCardNumber);
    boolean existsByUsername(String username);
}
