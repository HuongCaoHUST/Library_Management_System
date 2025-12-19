package com.example.project.service;

import com.example.project.dto.request.GRNRequest;
import com.example.project.dto.request.RoleRequest;
import com.example.project.mapper.GRNMapper;
import com.example.project.mapper.RoleMapper;
import com.example.project.model.*;
import com.example.project.repository.PermissionRepository;
import com.example.project.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    private final RoleMapper mapper;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy role");
        }
        roleRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    @Transactional
    public Role add (RoleRequest roleRequest) {

        if (roleRepository.existsByName(roleRequest.getName())) {
            throw new IllegalArgumentException("Role đã tồn tại");
        }

        Role role = mapper.toEntity(roleRequest);

        Set<Permission> permissions = roleRequest.getPermissions().stream()
                .map(d -> permissionRepository.findByName(d.getName())
                        .orElseThrow(() -> new RuntimeException("Permission not found: " + d.getName()))
                )
                .collect(Collectors.toSet());

        role.setPermissions(permissions);
        return roleRepository.save(role);
    }
}
