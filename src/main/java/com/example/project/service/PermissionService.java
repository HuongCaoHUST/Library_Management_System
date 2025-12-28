package com.example.project.service;

import com.example.project.dto.request.PermissionAddToRoleRequest;
import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.response.PermissionResponse;
import com.example.project.mapper.PermissionMapper;
import com.example.project.model.Permission;
import com.example.project.model.Role;
import com.example.project.repository.PermissionRepository;
import com.example.project.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
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

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {

            List<Role> roles = roleRepository.findAllByIdIn(request.getRoles());

            if (roles.size() != request.getRoles().size()) {
                throw new IllegalArgumentException("Một số Role không tồn tại");
            }

            for (Role role : roles) {
                Set<Permission> permissions = new HashSet<>(role.getPermissions());

                permissions.add(savedPermission);
                role.setPermissions(permissions);
            }
            roleRepository.saveAll(roles);
        }

        return mapper.toResponse(savedPermission);
    }

    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
}
