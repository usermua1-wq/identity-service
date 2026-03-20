package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.response.ApiResponse;
import com.devteria.identity_service.dto.request.AuthenticationRequest;
import com.devteria.identity_service.dto.request.VerifyTokenRequest;
import com.devteria.identity_service.dto.response.AuthenticationDTO;
import com.devteria.identity_service.dto.response.VerifyTokenDTO;
import com.devteria.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/login")
    ApiResponse<AuthenticationDTO> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        var result = authenticationService.authenticate(authenticationRequest);
        return ApiResponse.<AuthenticationDTO>builder()
                .result(result)
                .build();
    }

    @PostMapping("/verify")
    ApiResponse<VerifyTokenDTO> verifyToken(@RequestBody VerifyTokenRequest verifyTokenRequest)
            throws ParseException, JOSEException {
        var result = authenticationService.verifyToken(verifyTokenRequest);
        return ApiResponse.<VerifyTokenDTO>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody VerifyTokenRequest verifyTokenRequest)
            throws ParseException, JOSEException {
        authenticationService.logout(verifyTokenRequest);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationDTO> refreshToken(@RequestBody VerifyTokenRequest request) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationDTO>builder()
                .result(result)
                .build();
    }
}
