package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.request.AuthenticationRequest;
import com.devteria.identity_service.dto.request.VerifyTokenRequest;
import com.devteria.identity_service.dto.response.AuthenticationDTO;
import com.devteria.identity_service.dto.response.VerifyTokenDTO;
import com.devteria.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthenticationController REST endpoints.
 * Tests authentication operations: login, verify, logout, refresh.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationController Unit Tests")
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private AuthenticationRequest authenticationRequest;
    private AuthenticationDTO authenticationDTO;
    private VerifyTokenRequest verifyTokenRequest;
    private VerifyTokenDTO verifyTokenDTO;

    @BeforeEach
    void setUp() {
        authenticationRequest = AuthenticationRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        authenticationDTO = AuthenticationDTO.builder()
                .token("test-jwt-token")
                .build();

        verifyTokenRequest = VerifyTokenRequest.builder()
                .token("test-jwt-token")
                .build();

        verifyTokenDTO = VerifyTokenDTO.builder()
                .valid(true)
                .build();
    }

    @Test
    @DisplayName("Authenticate user - returns token")
    void testAuthenticateSuccess() throws ParseException, JOSEException {
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(authenticationDTO);

        var response = authenticationController.authenticate(authenticationRequest);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().getToken()).isEqualTo("test-jwt-token");
    }

    @Test
    @DisplayName("Verify token - returns token validity")
    void testVerifyTokenSuccess() throws ParseException, JOSEException {
        when(authenticationService.verifyToken(any(VerifyTokenRequest.class)))
                .thenReturn(verifyTokenDTO);

        var response = authenticationController.verifyToken(verifyTokenRequest);

        assertThat(response).isNotNull();
        assertThat(response.getResult().isValid()).isTrue();
    }

    @Test
    @DisplayName("Logout user - returns success response")
    void testLogoutSuccess() throws ParseException, JOSEException {
        doNothing().when(authenticationService).logout(any(VerifyTokenRequest.class));

        var response = authenticationController.logout(verifyTokenRequest);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Refresh token - returns new token")
    void testRefreshTokenSuccess() throws ParseException, JOSEException {
        when(authenticationService.refreshToken(any(VerifyTokenRequest.class)))
                .thenReturn(authenticationDTO);

        var response = authenticationController.refreshToken(verifyTokenRequest);

        assertThat(response).isNotNull();
        assertThat(response.getResult().getToken()).isEqualTo("test-jwt-token");
    }
}


