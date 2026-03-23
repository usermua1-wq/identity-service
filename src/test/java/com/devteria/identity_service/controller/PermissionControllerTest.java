package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.request.PermissionRequest;
import com.devteria.identity_service.dto.response.PermissionDTO;
import com.devteria.identity_service.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PermissionController REST endpoints.
 * Tests permission management operations: create, retrieve, delete.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionController Unit Tests")
class PermissionControllerTest {

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private PermissionController permissionController;

    private PermissionRequest permissionRequest;
    private PermissionDTO permissionDTO;

    @BeforeEach
    void setUp() {
        permissionRequest = PermissionRequest.builder()
                .permissionname("CREATE_USER")
                .description("Permission to create users")
                .build();

        permissionDTO = PermissionDTO.builder()
                .permissionname("CREATE_USER")
                .description("Permission to create users")
                .build();
    }

    @Test
    @DisplayName("Create permission - returns ApiResponse with created permission")
    void testCreatePermissionSuccess() {
        when(permissionService.createPermission(any(PermissionRequest.class)))
                .thenReturn(permissionDTO);

        var response = permissionController.createPermission(permissionRequest);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getPermissionname()).isEqualTo("CREATE_USER");
        assertThat(response.getResult().getDescription()).isEqualTo("Permission to create users");
    }

    @Test
    @DisplayName("Get all permissions - returns list of permissions")
    void testGetAllPermissionsSuccess() {
        PermissionDTO readPermission = PermissionDTO.builder()
                .permissionname("READ_USER")
                .description("Permission to read users")
                .build();

        List<PermissionDTO> permissionList = Arrays.asList(permissionDTO, readPermission);
        when(permissionService.getAll()).thenReturn(permissionList);

        var response = permissionController.getAll();

        assertThat(response).isNotNull();
        assertThat(response.getResult()).hasSize(2);
    }

    @Test
    @DisplayName("Get all permissions - returns empty list")
    void testGetAllPermissionsEmpty() {
        when(permissionService.getAll()).thenReturn(Arrays.asList());

        var response = permissionController.getAll();

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isEmpty();
    }

    @Test
    @DisplayName("Delete permission - returns success response")
    void testDeletePermissionSuccess() {
        doNothing().when(permissionService).delete("CREATE_USER");

        var response = permissionController.deletePermission("CREATE_USER");

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Delete non-existent permission - returns success response")
    void testDeleteNonExistentPermission() {
        doNothing().when(permissionService).delete("NONEXISTENT");

        var response = permissionController.deletePermission("NONEXISTENT");

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Get single permission - returns list with single permission")
    void testGetSinglePermission() {
        List<PermissionDTO> permissionList = Arrays.asList(permissionDTO);
        when(permissionService.getAll()).thenReturn(permissionList);

        var response = permissionController.getAll();

        assertThat(response).isNotNull();
        assertThat(response.getResult()).hasSize(1);
        assertThat(response.getResult().get(0).getPermissionname()).isEqualTo("CREATE_USER");
    }

    @Test
    @DisplayName("Create multiple permissions - handles correctly")
    void testCreateMultiplePermissions() {
        PermissionDTO deletePermission = PermissionDTO.builder()
                .permissionname("DELETE_USER")
                .description("Permission to delete users")
                .build();

        when(permissionService.createPermission(any(PermissionRequest.class)))
                .thenReturn(permissionDTO)
                .thenReturn(deletePermission);

        var createResponse = permissionController.createPermission(permissionRequest);
        var deleteResponse = permissionController.createPermission(
                PermissionRequest.builder()
                        .permissionname("DELETE_USER")
                        .description("Permission to delete users")
                        .build()
        );

        assertThat(createResponse.getResult().getPermissionname()).isEqualTo("CREATE_USER");
        assertThat(deleteResponse.getResult().getPermissionname()).isEqualTo("DELETE_USER");
    }
}







