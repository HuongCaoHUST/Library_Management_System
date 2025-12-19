package com.example.project.service;

import com.example.project.dto.request.LibrarianRequest;
import com.example.project.mapper.LibrarianMapper;
import com.example.project.model.Librarian;
import com.example.project.model.Role;
import com.example.project.repository.LibrarianRepository;
import com.example.project.repository.RoleRepository;
import com.example.project.specification.LibrarianSpecification;
import com.example.project.util.PasswordUtils;
import com.example.project.util.SendEmail;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final LibrarianMapper mapper;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy role");
        }
        roleRepository.deleteById(id);
    }

    public boolean existsByUsername(String name) {
        return roleRepository.existsByName(name);
    }

}
