package com.example.project.service;

import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.request.RoleRequest;
import com.example.project.dto.response.PermissionResponse;
import com.example.project.dto.response.RoleResponse;
import com.example.project.mapper.PermissionMapper;
import com.example.project.mapper.RoleMapper;
import com.example.project.model.Permission;
import com.example.project.model.Role;
import com.example.project.repository.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper mapper;

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy permission");
        }
        permissionRepository.deleteById(id);
    }

    @Transactional
    public PermissionResponse add (PermissionRequest request) {

        if (permissionRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Permission đã tồn tại");
        }
        Permission permission = mapper.toEntity(request);
        return mapper.toResponse(permission);
    }

    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
}
