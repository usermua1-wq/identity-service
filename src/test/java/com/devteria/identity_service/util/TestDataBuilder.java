package com.devteria.identity_service.util;

import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.response.UserDTO;
import com.devteria.identity_service.entity.User;

import java.time.LocalDate;

/**
 * Utility class for creating test data.
 * Provides factory methods for common test objects.
 */
public class TestDataBuilder {

    public static User.UserBuilder userBuilder() {
        return User.builder()
                .id("test-user-id")
                .username("testuser")
                .password("encoded-password")
                .firstName("Test")
                .lastName("User")
                .dob(LocalDate.of(1990, 1, 1));
    }

    public static User buildUser() {
        return userBuilder().build();
    }

    public static UserDTO.UserDTOBuilder userDTOBuilder() {
        return UserDTO.builder()
                .id("test-user-id")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .dob(LocalDate.of(1990, 1, 1));
    }

    public static UserDTO buildUserDTO() {
        return userDTOBuilder().build();
    }

    public static UserCreationRequest.UserCreationRequestBuilder userCreationRequestBuilder() {
        return UserCreationRequest.builder()
                .username("newuser")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .dob(LocalDate.of(1995, 5, 15));
    }

    public static UserCreationRequest buildUserCreationRequest() {
        return userCreationRequestBuilder().build();
    }
}

