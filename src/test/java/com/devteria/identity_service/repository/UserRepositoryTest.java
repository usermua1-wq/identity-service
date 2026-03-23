package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * JPA repository tests for UserRepository.
 * Tests database operations and query methods.
 */
@DataJpaTest
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .password("encoded-password")
                .firstName("Test")
                .lastName("User")
                .dob(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    @DisplayName("Should save user successfully")
    void testSaveUser() {
        // Act
        User savedUser = userRepository.save(testUser);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should find user by ID")
    void testFindUserById() {
        // Arrange
        User savedUser = userRepository.save(testUser);

        // Act
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should find user by username")
    void testFindUserByUsername() {
        // Arrange
        userRepository.save(testUser);

        // Act
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should return false when user does not exist by username")
    void testExistsByUsernameNotFound() {
        // Act
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return true when user exists by username")
    void testExistsByUsernameFound() {
        // Arrange
        userRepository.save(testUser);

        // Act
        boolean exists = userRepository.existsByUsername("testuser");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        savedUser.setFirstName("Updated");
        savedUser.setLastName("Name");

        // Act
        User updatedUser = userRepository.save(savedUser);

        // Assert
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
        assertThat(updatedUser.getLastName()).isEqualTo("Name");
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        // Arrange
        User savedUser = userRepository.save(testUser);
        String userId = savedUser.getId();

        // Act
        userRepository.deleteById(userId);

        // Assert
        assertThat(userRepository.findById(userId)).isEmpty();
    }
}

