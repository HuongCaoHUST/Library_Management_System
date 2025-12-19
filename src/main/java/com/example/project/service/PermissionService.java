package com.example.project.service;

import com.example.project.mapper.LibrarianMapper;
import com.example.project.model.Role;
import com.example.project.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
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
