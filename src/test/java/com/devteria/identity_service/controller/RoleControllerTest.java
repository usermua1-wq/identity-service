package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.request.RoleRequest;
import com.devteria.identity_service.dto.response.RoleDTO;
import com.devteria.identity_service.service.RoleService;
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
 * Unit tests for RoleController REST endpoints.
 * Tests role management operations: create, retrieve, delete.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleController Unit Tests")
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private RoleRequest roleRequest;
    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        roleRequest = RoleRequest.builder()
                .rolename("ADMIN")
                .description("Administrator role")
                .build();

        roleDTO = RoleDTO.builder()
                .rolename("ADMIN")
                .description("Administrator role")
                .build();
    }

    @Test
    @DisplayName("Create role - returns ApiResponse with created role")
    void testCreateRoleSuccess() {
        when(roleService.createRole(any(RoleRequest.class))).thenReturn(roleDTO);

        var response = roleController.createRole(roleRequest);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getRolename()).isEqualTo("ADMIN");
        assertThat(response.getResult().getDescription()).isEqualTo("Administrator role");
    }

    @Test
    @DisplayName("Get all roles - returns list of roles")
    void testGetAllRolesSuccess() {
        List<RoleDTO> roleList = Arrays.asList(roleDTO, roleDTO);
        when(roleService.getAll2()).thenReturn(roleList);

        var response = roleController.getAll2();

        assertThat(response).isNotNull();
        assertThat(response.getResult()).hasSize(2);
    }

    @Test
    @DisplayName("Get all roles - returns empty list")
    void testGetAllRolesEmpty() {
        when(roleService.getAll2()).thenReturn(Arrays.asList());

        var response = roleController.getAll2();

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isEmpty();
    }

    @Test
    @DisplayName("Delete role - returns success response")
    void testDeleteRoleSuccess() {
        doNothing().when(roleService).deleteRole("ADMIN");

        var response = roleController.deletePermission("ADMIN");

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Delete non-existent role - returns success response")
    void testDeleteNonExistentRole() {
        doNothing().when(roleService).deleteRole("NONEXISTENT");

        var response = roleController.deletePermission("NONEXISTENT");

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Create role with multiple roles - handles correctly")
    void testCreateMultipleRoles() {
        RoleDTO userRole = RoleDTO.builder()
                .rolename("USER")
                .description("User role")
                .build();

        when(roleService.createRole(any(RoleRequest.class)))
                .thenReturn(roleDTO)
                .thenReturn(userRole);

        var adminResponse = roleController.createRole(roleRequest);
        var userResponse = roleController.createRole(
                RoleRequest.builder().rolename("USER").description("User role").build()
        );

        assertThat(adminResponse.getResult().getRolename()).isEqualTo("ADMIN");
        assertThat(userResponse.getResult().getRolename()).isEqualTo("USER");
    }
}






