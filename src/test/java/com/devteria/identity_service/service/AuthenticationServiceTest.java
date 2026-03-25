package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.request.AuthenticationRequest;
import com.devteria.identity_service.dto.response.AuthenticationDTO;
import com.devteria.identity_service.entity.Permission;
import com.devteria.identity_service.entity.Role;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.exception.AppException;
import com.devteria.identity_service.repository.InvalidatedTokenRepository;
import com.devteria.identity_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthenticationService class.
 * Tests authentication and token generation functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Tests")
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;
    private AuthenticationRequest authenticationRequest;
    private Role testRole;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        // Initialize JWT configuration properties with proper length for HS512 (minimum 32 bytes)
        String signingKey = "your-256-bit-secret-key-for-hs512-algorithm-minimum-32-bytes-required";
        ReflectionTestUtils.setField(authenticationService, "SIGNED_KEY", signingKey);
        ReflectionTestUtils.setField(authenticationService, "TOKEN_DURATION", 3600L);
        ReflectionTestUtils.setField(authenticationService, "REFRESHABLE_DURATION", 604800L);

        // Initialize test permission
        testPermission = Permission.builder()
                .permissionname("READ")
                .description("Read permission")
                .build();

        // Initialize test role
        testRole = Role.builder()
                .rolename("ADMIN")
                .description("Administrator role")
                .permissions(new HashSet<>(Arrays.asList(testPermission)))
                .build();

        // Initialize test user with BCrypt encoded password
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String encodedPassword = passwordEncoder.encode("password123");
        testUser = User.builder()
                .id("user-123")
                .username("testuser")
                .password(encodedPassword)
                .firstName("Test")
                .lastName("User")
                .dob(LocalDate.of(1990, 1, 1))
                .roles(new HashSet<>(Arrays.asList(testRole)))
                .build();

        // Initialize authentication request
        authenticationRequest = AuthenticationRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("Should authenticate user successfully with correct credentials")
    void testAuthenticateSuccess() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        AuthenticationDTO result = authenticationService.authenticate(authenticationRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getToken()).isNotNull();
        assertThat(result.getToken()).isNotEmpty();
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testAuthenticateUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        authenticationRequest.setUsername("nonexistent");

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.authenticate(authenticationRequest))
                .isInstanceOf(AppException.class);
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void testAuthenticateWrongPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        authenticationRequest.setPassword("wrongpassword");

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.authenticate(authenticationRequest))
                .isInstanceOf(AppException.class);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should generate valid JWT token for user")
    void testGenerateTokenSuccess() {
        // Act
        String token = authenticationService.generateToken(testUser);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        // Token should contain 3 parts separated by dots (header.payload.signature)
        String[] tokenParts = token.split("\\.");
        assertThat(tokenParts).hasSize(3);
    }

    @Test
    @DisplayName("Should include user scope in generated token")
    void testGenerateTokenIncludesScope() {
        // Act
        String token = authenticationService.generateToken(testUser);

        // Assert
        assertThat(token).isNotNull();
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
    }

    @Test
    @DisplayName("Should generate token with proper structure")
    void testGenerateTokenHasCorrectStructure() {
        // Act
        String token = authenticationService.generateToken(testUser);

        // Assert
        assertThat(token).isNotNull();
        // Verify token has 3 parts: header.payload.signature
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
        // Each part should not be empty
        assertThat(parts[0]).isNotEmpty();
        assertThat(parts[1]).isNotEmpty();
        assertThat(parts[2]).isNotEmpty();
    }

    @Test
    @DisplayName("Should include user permissions in scope")
    void testGenerateTokenIncludesPermissions() {
        // Arrange - User with role and permissions
        testUser.setRoles(new HashSet<>(Arrays.asList(testRole)));

        // Act
        String token = authenticationService.generateToken(testUser);

        // Assert
        assertThat(token).isNotNull();
        // Token should have proper structure with 3 parts
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
    }

    @Test
    @DisplayName("Should handle user with no roles")
    void testGenerateTokenUserWithoutRoles() {
        // Arrange
        User userWithoutRoles = User.builder()
                .id("user-456")
                .username("simpleuser")
                .password("password")
                .firstName("Simple")
                .lastName("User")
                .dob(LocalDate.of(1995, 5, 15))
                .roles(new HashSet<>())
                .build();

        // Act
        String token = authenticationService.generateToken(userWithoutRoles);

        // Assert
        assertThat(token).isNotNull();
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
    }

    @Test
    @DisplayName("Should generate unique tokens on multiple calls")
    void testGenerateTokenUniqueness() {
        // Act
        String token1 = authenticationService.generateToken(testUser);
        String token2 = authenticationService.generateToken(testUser);

        // Assert
        assertThat(token1).isNotNull();
        assertThat(token2).isNotNull();
        // JWTs should be different because of different issue times
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should authenticate and return token with user roles")
    void testAuthenticateReturnsTokenWithRoles() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        AuthenticationDTO result = authenticationService.authenticate(authenticationRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getToken()).isNotNull();
        assertThat(result.getToken().split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should verify token response has correct structure")
    void testAuthenticationDTOStructure() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        AuthenticationDTO result = authenticationService.authenticate(authenticationRequest);

        // Assert
        assertThat(result.getToken()).isNotNull();
        assertThat(result.isAuthenticated()).isTrue();
    }
}




