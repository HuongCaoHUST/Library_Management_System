package com.example.project.service;

import com.example.project.model.Librarian;
import com.example.project.model.Reader;
import com.example.project.repository.ReaderRepository;
import com.example.project.specification.ReaderSpecification;
import com.example.project.util.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.example.project.util.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ReaderService {
    private final ReaderRepository repository;

    public ReaderService(ReaderRepository repository) {
        this.repository = repository;
    }

    public List<Reader> findAll() {
        return repository.findAll();
    }

    public Optional<Reader> findById(Long id) {
        return repository.findById(id);
    }

    public Reader save(Reader reader) {
        return repository.save(reader);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private SendEmail sendEmail;

    public Reader registerReader(Reader inputReader) {
        String email = inputReader.getEmail().trim().toLowerCase();
        if (repository.findAll().stream().anyMatch(r -> r.getEmail().equalsIgnoreCase(inputReader.getEmail()))) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }

        if (repository.findAll().stream().anyMatch(r -> r.getIdCardNumber().equals(inputReader.getIdCardNumber()))) {
            throw new IllegalArgumentException("Số CCCD đã được đăng ký!");
        }

        String username = email;
        Reader reader = Reader.builder()
                .fullName(inputReader.getFullName())
                .gender(inputReader.getGender())
                .birthDate(inputReader.getBirthDate())
                .phoneNumber(inputReader.getPhoneNumber())
                .email(inputReader.getEmail())
                .idCardNumber(inputReader.getIdCardNumber())
                .placeOfBirth(inputReader.getPlaceOfBirth())
                .issuedPlace(inputReader.getIssuedPlace())
                .major(inputReader.getMajor())
                .workPlace(inputReader.getWorkPlace())
                .address(inputReader.getAddress())
                .username(username)
                .password(null)
                .registrationDate(LocalDateTime.now())
                .status("PENDING")
                .depositAmount(BigDecimal.ZERO)
                .build();

        return repository.save(reader);
    }

    public List<Reader> getPendingReaders() {
        return readerRepository.findByStatus("PENDING");
    }

    public List<Reader> getApprovedReaders() {
        return readerRepository.findByStatus("APPROVED");
    }
    public List<Reader> filterReaders(String fullName, String email, String status, String gender) {
        Specification<Reader> spec = Specification
                .where(ReaderSpecification.hasFullName(fullName))
                .and(ReaderSpecification.hasEmail(email))
                .and(ReaderSpecification.hasStatus(status))
                .and(ReaderSpecification.hasGender(gender));

        return repository.findAll(spec);
    }
}
