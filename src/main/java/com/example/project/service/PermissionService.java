package com.example.project.service;

import com.example.project.dto.request.PermissionAddToRoleRequest;
import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.request.RolePermissionRequest;
import com.example.project.dto.response.PermissionResponse;
import com.example.project.mapper.PermissionMapper;
import com.example.project.model.Permission;
import com.example.project.model.Role;
import com.example.project.repository.PermissionRepository;
import com.example.project.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionMapper mapper;
    private final RoleService roleService;

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
        Permission saved = permissionRepository.save(permission);
        return mapper.toResponse(saved);
    }

    @Transactional
    public PermissionResponse add_to_role (PermissionAddToRoleRequest request) {

        if (permissionRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Permission đã tồn tại");
        }
        Permission permission = mapper.toEntity(request);
        Permission savedPermission = permissionRepository.save(permission);

        if (request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            return mapper.toResponse(savedPermission);
        }

        List<Role> roles = roleRepository.findAllById(request.getRoleIds());

        for (Role role : roles) {
            if (role.getPermissions() == null) {
                role.setPermissions(new HashSet<>());
            }
            role.getPermissions().add(savedPermission);
        }

        return mapper.toResponse(savedPermission);
    }

    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
}
