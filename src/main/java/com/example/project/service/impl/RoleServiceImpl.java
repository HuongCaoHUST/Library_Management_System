package com.example.project.service.impl;

import com.example.project.dto.request.RolePermissionRequest;
import com.example.project.dto.request.RoleRequest;
import com.example.project.dto.response.RoleResponse;
import com.example.project.mapper.RoleMapper;
import com.example.project.model.Permission;
import com.example.project.model.Role;
import com.example.project.repository.PermissionRepository;
import com.example.project.repository.RoleRepository;
import com.example.project.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper mapper;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy role");
        }
        roleRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    @Override
    @Transactional
    public RoleResponse add(RoleRequest roleRequest) {

        if (roleRepository.existsByName(roleRequest.getName())) {
            throw new IllegalArgumentException("Role đã tồn tại");
        }

        Role role = mapper.toEntity(roleRequest);

        Set<Permission> permissions = roleRequest.getPermissions().stream()
                .map(p -> permissionRepository.findByName(p.getName())
                        .orElseThrow(() ->
                                new RuntimeException("Permission not found: " + p.getName()))
                )
                .collect(Collectors.toSet());

        role.setPermissions(permissions);

        Role saved = roleRepository.save(role);

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RoleResponse addPermissionsToRole(RolePermissionRequest request) {

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại"));

        List<Permission> permissions =
                permissionRepository.findAllById(request.getPermissionIds());

        if (permissions.isEmpty()) {
            throw new IllegalArgumentException("Danh sách permission không hợp lệ");
        }

        role.getPermissions().addAll(permissions);

        roleRepository.save(role);

        return new RoleResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse removePermissionFromRole(Long roleId, Long permissionId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission không tồn tại"));

        boolean removed = role.getPermissions().remove(permission);

        if (!removed) {
            throw new IllegalArgumentException("Permission không thuộc role này");
        }

        Role saved = roleRepository.save(role);

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RoleResponse removePermissionsFromRole(Long roleId, List<Long> permissionIds) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại"));

        List<Permission> permissions =
                permissionRepository.findAllById(permissionIds);

        if (permissions.isEmpty()) {
            throw new IllegalArgumentException("Danh sách permission không hợp lệ");
        }

        boolean removedAny = role.getPermissions().removeAll(permissions);

        if (!removedAny) {
            throw new IllegalArgumentException("Không permission nào thuộc role này");
        }

        Role saved = roleRepository.save(role);

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RoleResponse replacePermissions(Long roleId, List<Long> permissionIds) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại"));

        List<Permission> permissions =
                permissionRepository.findAllById(permissionIds);

        if (permissions.isEmpty() && !permissionIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách permission không hợp lệ");
        }

        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);

        Role saved = roleRepository.save(role);

        return mapper.toResponse(saved);
    }


}
