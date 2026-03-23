package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.request.UserUpdateRequest;
import com.devteria.identity_service.dto.response.UserDTO;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserController REST endpoints.
 * Tests controller logic with mocked UserService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Unit Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserDTO testUserDTO;
    private UserCreationRequest userCreationRequest;
    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-123")
                .username("testuser")
                .password("encoded-password")
                .firstName("Test")
                .lastName("User")
                .dob(LocalDate.of(1990, 1, 1))
                .roles(new HashSet<>())
                .build();

        testUserDTO = UserDTO.builder()
                .id("user-123")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .dob(LocalDate.of(1990, 1, 1))
                .build();

        userCreationRequest = UserCreationRequest.builder()
                .username("newuser")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .dob(LocalDate.of(1995, 5, 15))
                .build();

        userUpdateRequest = UserUpdateRequest.builder()
                .userId("user-123")
                .password("newpassword")
                .firstName("Updated")
                .lastName("User")
                .roles(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Create user - returns ApiResponse with created user")
    void testCreateUserSuccess() {
        when(userService.createRequest(any(UserCreationRequest.class))).thenReturn(testUser);

        var response = userController.createUser(userCreationRequest);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getId()).isEqualTo("user-123");
        assertThat(response.getResult().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Get all users - returns list of users")
    void testGetAllUsersSuccess() {
        List<UserDTO> userList = Arrays.asList(testUserDTO, testUserDTO);
        when(userService.getUsers()).thenReturn(userList);

        var response = userController.getUsers();

        assertThat(response).isNotNull();
        assertThat(response.getResult()).hasSize(2);
    }

    @Test
    @DisplayName("Get user by ID - returns user details")
    void testGetUserByIdSuccess() {
        when(userService.getUser("user-123")).thenReturn(testUserDTO);

        var response = userController.getUser("user-123");

        assertThat(response).isNotNull();
        assertThat(response.getResult().getId()).isEqualTo("user-123");
        assertThat(response.getResult().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Update user - returns updated user")
    void testUpdateUserSuccess() {
        when(userService.userUpdate(any(UserUpdateRequest.class))).thenReturn(testUserDTO);

        var response = userController.userUpdate(userUpdateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getResult().getId()).isEqualTo("user-123");
    }

    @Test
    @DisplayName("Delete user - returns success message")
    void testDeleteUserSuccess() {
        doNothing().when(userService).deleteUser("user-123");

        var response = userController.deleteUser("user-123");

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isEqualTo("User has been deleted");
    }

    @Test
    @DisplayName("Get my info - returns current user info")
    void testGetMyInfoSuccess() {
        when(userService.getMyInfo()).thenReturn(testUserDTO);

        var response = userController.getMyInfo();

        assertThat(response).isNotNull();
        assertThat(response.getResult().getId()).isEqualTo("user-123");
        assertThat(response.getResult().getUsername()).isEqualTo("testuser");
    }
}


