package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.request.PermissionRequest;
import com.devteria.identity_service.dto.response.PermissionDTO;
import com.devteria.identity_service.entity.Permission;
import com.devteria.identity_service.mapper.PermissionMapper;
import com.devteria.identity_service.repository.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PermissionService class.
 * Tests permission creation, retrieval, and deletion functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionService Tests")
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionService permissionService;

    private Permission testPermission;
    private PermissionDTO testPermissionDTO;
    private PermissionRequest permissionRequest;

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

        // Initialize permission request
        permissionRequest = PermissionRequest.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();
    }

    @Test
    @DisplayName("Should create permission successfully")
    void testCreatePermissionSuccess() {
        // Arrange
        when(permissionMapper.toPermission(permissionRequest)).thenReturn(testPermission);
        when(permissionRepository.save(any(Permission.class))).thenReturn(testPermission);
        when(permissionMapper.toPermissionResponse(testPermission)).thenReturn(testPermissionDTO);

        // Act
        PermissionDTO result = permissionService.createPermission(permissionRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPermissionname()).isEqualTo("READ");
        assertThat(result.getDescription()).isEqualTo("Read permission");
        verify(permissionRepository).save(any(Permission.class));
        verify(permissionMapper).toPermission(permissionRequest);
        verify(permissionMapper).toPermissionResponse(testPermission);
    }

    @Test
    @DisplayName("Should retrieve all permissions successfully")
    void testGetAllPermissionsSuccess() {
        // Arrange
        Permission permission2 = Permission.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .creDate(LocalDateTime.now())
                .updDate(LocalDateTime.now())
                .createdBy("admin")
                .updatedBy("admin")
                .build();

        Permission permission3 = Permission.builder()
                .permissionname("DELETE")
                .description("Delete permission")
                .creDate(LocalDateTime.now())
                .updDate(LocalDateTime.now())
                .createdBy("admin")
                .updatedBy("admin")
                .build();

        List<Permission> permissions = Arrays.asList(testPermission, permission2, permission3);

        PermissionDTO permissionDTO2 = PermissionDTO.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        PermissionDTO permissionDTO3 = PermissionDTO.builder()
                .permissionname("DELETE")
                .description("Delete permission")
                .build();

        when(permissionRepository.findAll()).thenReturn(permissions);
        when(permissionMapper.toPermissionResponse(testPermission)).thenReturn(testPermissionDTO);
        when(permissionMapper.toPermissionResponse(permission2)).thenReturn(permissionDTO2);
        when(permissionMapper.toPermissionResponse(permission3)).thenReturn(permissionDTO3);

        // Act
        List<PermissionDTO> result = permissionService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).contains(testPermissionDTO, permissionDTO2, permissionDTO3);
        verify(permissionRepository).findAll();
        verify(permissionMapper, times(3)).toPermissionResponse(any(Permission.class));
    }

    @Test
    @DisplayName("Should return empty list when no permissions exist")
    void testGetAllPermissionsEmpty() {
        // Arrange
        when(permissionRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<PermissionDTO> result = permissionService.getAll();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(permissionRepository).findAll();
    }

    @Test
    @DisplayName("Should delete permission successfully")
    void testDeletePermissionSuccess() {
        // Arrange
        doNothing().when(permissionRepository).deleteById("READ");

        // Act
        permissionService.delete("READ");

        // Assert
        verify(permissionRepository).deleteById("READ");
    }

    @Test
    @DisplayName("Should handle deletion of non-existent permission gracefully")
    void testDeleteNonExistentPermission() {
        // Arrange
        doNothing().when(permissionRepository).deleteById("NONEXISTENT");

        // Act
        permissionService.delete("NONEXISTENT");

        // Assert
        verify(permissionRepository).deleteById("NONEXISTENT");
    }

    @Test
    @DisplayName("Should handle null permission name in delete")
    void testDeleteWithNullPermissionName() {
        // Arrange
        doNothing().when(permissionRepository).deleteById(null);

        // Act
        permissionService.delete(null);

        // Assert
        verify(permissionRepository).deleteById(null);
    }

    @Test
    @DisplayName("Should map permission request to permission entity correctly")
    void testCreatePermissionMappingCorrect() {
        // Arrange
        Permission mappedPermission = Permission.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        PermissionDTO expectedDTO = PermissionDTO.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        when(permissionMapper.toPermission(permissionRequest)).thenReturn(mappedPermission);
        when(permissionRepository.save(mappedPermission)).thenReturn(mappedPermission);
        when(permissionMapper.toPermissionResponse(mappedPermission)).thenReturn(expectedDTO);

        // Act
        PermissionDTO result = permissionService.createPermission(permissionRequest);

        // Assert
        assertThat(result.getPermissionname()).isEqualTo("WRITE");
        assertThat(result.getDescription()).isEqualTo("Write permission");
    }

    @Test
    @DisplayName("Should create multiple permissions successfully")
    void testCreateMultiplePermissions() {
        // Arrange
        Permission permission1 = Permission.builder()
                .permissionname("READ")
                .description("Read permission")
                .build();

        Permission permission2 = Permission.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        PermissionDTO dto1 = PermissionDTO.builder()
                .permissionname("READ")
                .description("Read permission")
                .build();

        PermissionDTO dto2 = PermissionDTO.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        when(permissionMapper.toPermission(any(PermissionRequest.class)))
                .thenReturn(permission1, permission2);
        when(permissionRepository.save(any(Permission.class)))
                .thenReturn(permission1, permission2);
        when(permissionMapper.toPermissionResponse(any(Permission.class)))
                .thenReturn(dto1, dto2);

        PermissionRequest request1 = PermissionRequest.builder()
                .permissionname("READ")
                .description("Read permission")
                .build();

        PermissionRequest request2 = PermissionRequest.builder()
                .permissionname("WRITE")
                .description("Write permission")
                .build();

        // Act
        PermissionDTO result1 = permissionService.createPermission(request1);
        PermissionDTO result2 = permissionService.createPermission(request2);

        // Assert
        assertThat(result1.getPermissionname()).isEqualTo("READ");
        assertThat(result2.getPermissionname()).isEqualTo("WRITE");
        verify(permissionRepository, times(2)).save(any(Permission.class));
    }
}

