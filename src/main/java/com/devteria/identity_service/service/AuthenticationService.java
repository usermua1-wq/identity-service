package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.request.AuthenticationRequest;
import com.devteria.identity_service.dto.request.VerifyTokenRequest;
import com.devteria.identity_service.dto.response.AuthenticationResponse;
import com.devteria.identity_service.dto.response.VerifyTokenResponse;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.exception.AppException;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    @NonFinal
    @Value("${spring.jwt.signingKey}")
    protected String SIGNED_KEY;

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PasswordEncoder authenticated = new BCryptPasswordEncoder(10);
        if(!authenticated.matches(request.getPassword(), user.getPassword())){
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }else{
            var token = generateToken(user);
            return AuthenticationResponse.builder()
                    .token(token)
                    .authenticated(true)
                    .build();
        }
    }

    public String generateToken(User user) {
        // Secret key for HMAC (should be stored securely)
        byte[] secretBytes = SIGNED_KEY.getBytes();

        // Set JWT claims
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(new Date())
                .issuer("Self_Learning_Staff")
                .expirationTime(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hour expiry
                .claim("scope", buildScope(user))
                .build();

        // Create JWS header with HS512 algorithm
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // Create signed JWT
        SignedJWT signedJWT = new SignedJWT(header, claims);

        try {
            // Sign the JWT
            JWSSigner signer = new MACSigner(secretBytes);
            signedJWT.sign(signer);

            // Return JWT as string
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Token generation failed", e);
        }
    }

    public VerifyTokenResponse verifyToken(VerifyTokenRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(request.getToken());
        byte[] secretBytes = SIGNED_KEY.getBytes();
        JWSVerifier verifier = new MACVerifier(secretBytes);
        return VerifyTokenResponse.builder()
                .valid(signedJWT.verify(verifier) && new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()))
                .build();
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(", ");
        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(stringJoiner::add);
        }
        return stringJoiner.toString();
    }
}
