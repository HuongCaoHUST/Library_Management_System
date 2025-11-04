package com.example.project.service;

import com.example.project.model.Reader;
import com.example.project.repository.ReaderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public Reader registerReader(Reader inputReader) {
        String email = inputReader.getEmail().trim().toLowerCase();
        if (repository.findAll().stream().anyMatch(r -> r.getEmail().equalsIgnoreCase(inputReader.getEmail()))) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }

        if (repository.findAll().stream().anyMatch(r -> r.getIdCardNumber().equals(inputReader.getIdCardNumber()))) {
            throw new IllegalArgumentException("Số CCCD đã được đăng ký!");
        }

//        String username = inputReader.getEmail().split("@")[0].toLowerCase();
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
}
