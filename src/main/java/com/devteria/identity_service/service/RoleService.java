package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.request.RoleRequest;
import com.devteria.identity_service.dto.response.PermissionDTO;
import com.devteria.identity_service.dto.response.RoleDTO;
import com.devteria.identity_service.entity.Permission;
import com.devteria.identity_service.entity.Role;
import com.devteria.identity_service.mapper.PermissionMapper;
import com.devteria.identity_service.mapper.RoleMapper;
import com.devteria.identity_service.repository.PermissionRepository;
import com.devteria.identity_service.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository  permissionRepository;
    RoleMapper roleMapper;
    PermissionMapper permissionMapper;

    public RoleDTO createRole(RoleRequest roleRequest) {
        Role role = roleMapper.toRole(roleRequest);
        var permission = permissionRepository.findAllById(roleRequest.getPermissions());
        role.setPermissions(new HashSet<>(permission));
        roleRepository.save(role);
        RoleDTO roleDTO = roleMapper.tRoleResponse(role);
        roleDTO.setPermissions(permission.stream()
                .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toSet()));
        return roleDTO;
    }

    public List<RoleDTO> getAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(role -> {
            Set<PermissionDTO> permissionDTOs = role.getPermissions().stream()
                    .map(permissionMapper::toPermissionResponse) // Giả sử đây là hàm map của bạn
                    .collect(Collectors.toSet());
            return RoleDTO.builder()
                    .rolename(role.getRolename())
                    .description(role.getDescription())
                    .permissions(permissionDTOs) // Gán tập hợp đã convert vào đây
                    .build();
        }).collect(Collectors.toList());
    }

    public List<RoleDTO> getAll2() {
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> roleDTOS =  roles.stream().map(roleMapper::tRoleResponse).collect(Collectors.toCollection(ArrayList::new));
        return roleDTOS;
    }

    public void deleteRole(String rolename) {
        roleRepository.deleteById(rolename);
    }
}
