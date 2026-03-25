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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RoleService class.
 * Tests role creation, retrieval, and deletion functionality with permissions.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleService Tests")
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private RoleService roleService;

    private Role testRole;
    private RoleDTO testRoleDTO;
    private RoleRequest roleRequest;
    private Permission testPermission;
    private PermissionDTO testPermissionDTO;

    @BeforeEach
    void setUp() {
        // Initialize test permission
        testPermission = Permission.builder()
                .permissionname("READ")
                .description("Read permission")
                .creDate(LocalDateTime.now())
                .updDate(LocalDateTime.now())
                .createdBy("admin")
                .updatedBy("admin")
                .build();

        // Initialize test permission DTO
        testPermissionDTO = PermissionDTO.builder()
                .permissionname("READ")
                .description("Read permission")
                .build();

        // Initialize test role
        testRole = Role.builder()
                .rolename("ADMIN")
                .description("Administrator role")
                .permissions(new HashSet<>(Arrays.asList(testPermission)))
                .build();

        // Initialize test role DTO
        testRoleDTO = RoleDTO.builder()
                .rolename("ADMIN")
                .description("Administrator role")
                .permissions(new HashSet<>(Arrays.asList(testPermissionDTO)))
                .build();

        // Initialize role request
        roleRequest = RoleRequest.builder()
                .rolename("USER")
                .description("User role")
                .permissions(new HashSet<>(Arrays.asList("READ")))
                .build();
    }

    @Test
    @DisplayName("Should create role with permissions successfully")
    void testCreateRoleSuccess() {
        // Arrange
        when(roleMapper.toRole(roleRequest)).thenReturn(testRole);
        when(permissionRepository.findAllById(roleRequest.getPermissions()))
                .thenReturn(Arrays.asList(testPermission));
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);
        when(roleMapper.tRoleResponse(testRole)).thenReturn(testRoleDTO);
        when(permissionMapper.toPermissionResponse(testPermission)).thenReturn(testPermissionDTO);

        // Act
        RoleDTO result = roleService.createRole(roleRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRolename()).isEqualTo("ADMIN");
        assertThat(result.getDescription()).isEqualTo("Administrator role");
        assertThat(result.getPermissions()).hasSize(1);
        assertThat(result.getPermissions()).contains(testPermissionDTO);
        verify(roleRepository).save(any(Role.class));
        verify(roleMapper).toRole(roleRequest);
        verify(permissionRepository).findAllById(roleRequest.getPermissions());
    }

    @Test
    @DisplayName("Should create role with multiple permissions successfully")
    void testCreateRoleWithMultiplePermissionsSuccess() {
        // Arrange
        Permission permission2 = Permission.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        PermissionDTO permissionDTO2 = PermissionDTO.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        RoleRequest multiPermissionRequest = RoleRequest.builder()
                .rolename("MANAGER")
                .description("Manager role")
                .permissions(new HashSet<>(Arrays.asList("READ", "WRITE")))
                .build();

        Role roleWithMultiPermissions = Role.builder()
                .rolename("MANAGER")
                .description("Manager role")
                .permissions(new HashSet<>(Arrays.asList(testPermission, permission2)))
                .build();

        RoleDTO roleDTOWithMultiPermissions = RoleDTO.builder()
                .rolename("MANAGER")
                .description("Manager role")
                .permissions(new HashSet<>(Arrays.asList(testPermissionDTO, permissionDTO2)))
                .build();

        when(roleMapper.toRole(multiPermissionRequest)).thenReturn(roleWithMultiPermissions);
        when(permissionRepository.findAllById(multiPermissionRequest.getPermissions()))
                .thenReturn(Arrays.asList(testPermission, permission2));
        when(roleRepository.save(any(Role.class))).thenReturn(roleWithMultiPermissions);
        when(roleMapper.tRoleResponse(roleWithMultiPermissions)).thenReturn(roleDTOWithMultiPermissions);
        when(permissionMapper.toPermissionResponse(testPermission)).thenReturn(testPermissionDTO);
        when(permissionMapper.toPermissionResponse(permission2)).thenReturn(permissionDTO2);

        // Act
        RoleDTO result = roleService.createRole(multiPermissionRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRolename()).isEqualTo("MANAGER");
        assertThat(result.getPermissions()).hasSize(2);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    @DisplayName("Should create role with no permissions successfully")
    void testCreateRoleWithoutPermissionsSuccess() {
        // Arrange
        RoleRequest noPermissionRequest = RoleRequest.builder()
                .rolename("GUEST")
                .description("Guest role")
                .permissions(new HashSet<>())
                .build();

        Role roleWithoutPermissions = Role.builder()
                .rolename("GUEST")
                .description("Guest role")
                .permissions(new HashSet<>())
                .build();

        RoleDTO roleDTOWithoutPermissions = RoleDTO.builder()
                .rolename("GUEST")
                .description("Guest role")
                .permissions(new HashSet<>())
                .build();

        when(roleMapper.toRole(noPermissionRequest)).thenReturn(roleWithoutPermissions);
        when(permissionRepository.findAllById(noPermissionRequest.getPermissions()))
                .thenReturn(new ArrayList<>());
        when(roleRepository.save(any(Role.class))).thenReturn(roleWithoutPermissions);
        when(roleMapper.tRoleResponse(roleWithoutPermissions)).thenReturn(roleDTOWithoutPermissions);

        // Act
        RoleDTO result = roleService.createRole(noPermissionRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRolename()).isEqualTo("GUEST");
        assertThat(result.getPermissions()).isEmpty();
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    @DisplayName("Should retrieve all roles successfully")
    void testGetAllRolesSuccess() {
        // Arrange
        Permission permission2 = Permission.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        PermissionDTO permissionDTO2 = PermissionDTO.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        Role role2 = Role.builder()
                .rolename("USER")
                .description("User role")
                .permissions(new HashSet<>(Arrays.asList(testPermission, permission2)))
                .build();

        List<Role> roles = Arrays.asList(testRole, role2);

        RoleDTO roleDTO2 = RoleDTO.builder()
                .rolename("USER")
                .description("User role")
                .permissions(new HashSet<>(Arrays.asList(testPermissionDTO, permissionDTO2)))
                .build();

        when(roleRepository.findAll()).thenReturn(roles);
        when(permissionMapper.toPermissionResponse(testPermission)).thenReturn(testPermissionDTO);
        when(permissionMapper.toPermissionResponse(permission2)).thenReturn(permissionDTO2);

        // Act
        List<RoleDTO> result = roleService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRolename()).isEqualTo("ADMIN");
        assertThat(result.get(1).getRolename()).isEqualTo("USER");
        verify(roleRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no roles exist")
    void testGetAllRolesEmpty() {
        // Arrange
        when(roleRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<RoleDTO> result = roleService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(roleRepository).findAll();
    }

    @Test
    @DisplayName("Should get all roles using getAll2 successfully")
    void testGetAll2RolesSuccess() {
        // Arrange
        Role role2 = Role.builder()
                .rolename("USER")
                .description("User role")
                .permissions(new HashSet<>(Arrays.asList(testPermission)))
                .build();

        List<Role> roles = Arrays.asList(testRole, role2);

        RoleDTO roleDTO2 = RoleDTO.builder()
                .rolename("USER")
                .description("User role")
                .permissions(new HashSet<>())
                .build();

        when(roleRepository.findAll()).thenReturn(roles);
        when(roleMapper.tRoleResponse(testRole)).thenReturn(testRoleDTO);
        when(roleMapper.tRoleResponse(role2)).thenReturn(roleDTO2);

        // Act
        List<RoleDTO> result = roleService.getAll2();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(roleRepository).findAll();
        verify(roleMapper, times(2)).tRoleResponse(any(Role.class));
    }

    @Test
    @DisplayName("Should delete role successfully")
    void testDeleteRoleSuccess() {
        // Arrange
        doNothing().when(roleRepository).deleteById("ADMIN");

        // Act
        roleService.deleteRole("ADMIN");

        // Assert
        verify(roleRepository).deleteById("ADMIN");
    }

    @Test
    @DisplayName("Should handle deletion of non-existent role gracefully")
    void testDeleteNonExistentRole() {
        // Arrange
        doNothing().when(roleRepository).deleteById("NONEXISTENT");

        // Act
        roleService.deleteRole("NONEXISTENT");

        // Assert
        verify(roleRepository).deleteById("NONEXISTENT");
    }

    @Test
    @DisplayName("Should map role request to role entity correctly")
    void testCreateRoleMappingCorrect() {
        // Arrange
        Role mappedRole = Role.builder()
                .rolename("USER")
                .description("User role")
                .permissions(new HashSet<>())
                .build();

        RoleDTO expectedDTO = RoleDTO.builder()
                .rolename("USER")
                .description("User role")
                .permissions(new HashSet<>())
                .build();

        when(roleMapper.toRole(roleRequest)).thenReturn(mappedRole);
        when(permissionRepository.findAllById(roleRequest.getPermissions()))
                .thenReturn(Arrays.asList(testPermission));
        when(roleRepository.save(mappedRole)).thenReturn(mappedRole);
        when(roleMapper.tRoleResponse(mappedRole)).thenReturn(expectedDTO);

        // Act
        RoleDTO result = roleService.createRole(roleRequest);

        // Assert
        assertThat(result.getRolename()).isEqualTo("USER");
        assertThat(result.getDescription()).isEqualTo("User role");
    }

    @Test
    @DisplayName("Should create multiple roles successfully")
    void testCreateMultipleRoles() {
        // Arrange
        Role role1 = Role.builder()
                .rolename("ADMIN")
                .description("Admin role")
                .permissions(new HashSet<>(Arrays.asList(testPermission)))
                .build();

        Role role2 = Role.builder()
                .rolename("USER")
                .description("User role")
                .permissions(new HashSet<>(Arrays.asList(testPermission)))
                .build();

        RoleDTO dto1 = RoleDTO.builder()
                .rolename("ADMIN")
                .description("Admin role")
                .permissions(new HashSet<>(Arrays.asList(testPermissionDTO)))
                .build();

        RoleDTO dto2 = RoleDTO.builder()
                .rolename("USER")
                .description("User role")
                .permissions(new HashSet<>(Arrays.asList(testPermissionDTO)))
                .build();

        RoleRequest request1 = RoleRequest.builder()
                .rolename("ADMIN")
                .description("Admin role")
                .permissions(new HashSet<>(Arrays.asList("READ")))
                .build();

        RoleRequest request2 = RoleRequest.builder()
                .rolename("USER")
                .description("User role")
                .permissions(new HashSet<>(Arrays.asList("READ")))
                .build();

        when(roleMapper.toRole(request1)).thenReturn(role1);
        when(roleMapper.toRole(request2)).thenReturn(role2);
        when(permissionRepository.findAllById(request1.getPermissions()))
                .thenReturn(Arrays.asList(testPermission));
        when(permissionRepository.findAllById(request2.getPermissions()))
                .thenReturn(Arrays.asList(testPermission));
        when(roleRepository.save(role1)).thenReturn(role1);
        when(roleRepository.save(role2)).thenReturn(role2);
        when(roleMapper.tRoleResponse(role1)).thenReturn(dto1);
        when(roleMapper.tRoleResponse(role2)).thenReturn(dto2);
        when(permissionMapper.toPermissionResponse(testPermission)).thenReturn(testPermissionDTO);

        // Act
        RoleDTO result1 = roleService.createRole(request1);
        RoleDTO result2 = roleService.createRole(request2);

        // Assert
        assertThat(result1.getRolename()).isEqualTo("ADMIN");
        assertThat(result2.getRolename()).isEqualTo("USER");
        verify(roleRepository, times(2)).save(any(Role.class));
    }

    @Test
    @DisplayName("Should retrieve all roles with correct permissions mapping")
    void testGetAllRolesWithPermissionsMappingCorrect() {
        // Arrange
        Permission permission2 = Permission.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        PermissionDTO permissionDTO2 = PermissionDTO.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        Role role2 = Role.builder()
                .rolename("USER")
                .description("User role")
                .permissions(new HashSet<>(Arrays.asList(testPermission, permission2)))
                .build();

        List<Role> roles = Arrays.asList(role2);

        when(roleRepository.findAll()).thenReturn(roles);
        when(permissionMapper.toPermissionResponse(testPermission)).thenReturn(testPermissionDTO);
        when(permissionMapper.toPermissionResponse(permission2)).thenReturn(permissionDTO2);

        // Act
        List<RoleDTO> result = roleService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPermissions()).hasSize(2);
        verify(permissionMapper, times(2)).toPermissionResponse(any(Permission.class));
    }
}

