package com.example.project.service;

import com.example.project.model.Permission;
import com.example.project.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy permission");
        }
        permissionRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
}
