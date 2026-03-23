package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.request.AuthenticationRequest;
import com.devteria.identity_service.dto.request.VerifyTokenRequest;
import com.devteria.identity_service.dto.response.AuthenticationDTO;
import com.devteria.identity_service.dto.response.VerifyTokenDTO;
import com.devteria.identity_service.entity.InvalidatedToken;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.exception.AppException;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.repository.InvalidatedTokenRepository;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    @NonFinal
    @Value("${spring.jwt.signingKey}")
    protected String SIGNED_KEY;
    @NonFinal
    @Value("${spring.jwt.token-duration}")
    protected long TOKEN_DURATION;
    @NonFinal
    @Value("${spring.jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public AuthenticationDTO authenticate(AuthenticationRequest request){
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PasswordEncoder authenticated = new BCryptPasswordEncoder(10);
        if(!authenticated.matches(request.getPassword(), user.getPassword())){
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }else{
            var token = generateToken(user);
            return AuthenticationDTO.builder()
                    .token(token)
                    .authenticated(true)
                    .build();
        }
    }

    public void logout(VerifyTokenRequest request) throws ParseException, JOSEException {
        try {
            var signedToken = verify(request.getToken(), false);
            String jti = signedToken.getJWTClaimsSet().getJWTID();
            Date expirationTime = signedToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jti)
                    .expiryDate(expirationTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
          log.info(ErrorCode.TOKEN_EXPIRED.getMessage()) ;
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
                .expirationTime(new Date(Instant.now().plus(TOKEN_DURATION, ChronoUnit.SECONDS).toEpochMilli())) // 1 hour expiry
                .jwtID(UUID.randomUUID().toString())
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

    public AuthenticationDTO refreshToken(VerifyTokenRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = verify(request.getToken(), true);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryDate(expirationTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        var username = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        var token = generateToken(user);
        return AuthenticationDTO.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public VerifyTokenDTO verifyToken(VerifyTokenRequest request) throws ParseException, JOSEException {
        boolean valid = true;
        try {
            verify(request.getToken(), false);
        }catch (AppException e){
            valid = false;
        }
        return VerifyTokenDTO.builder()
                .valid(valid)
                .build();
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_"+role.getRolename());
                if(!CollectionUtils.isEmpty(role.getPermissions())){
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getPermissionname()));
                }
            });
        }
        return stringJoiner.toString();
    }

    private SignedJWT verify(String token, boolean isRefresh) throws ParseException, JOSEException {
        byte[] secretBytes = SIGNED_KEY.getBytes();
        JWSVerifier verifier = new MACVerifier(secretBytes);
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiration;
        if(isRefresh){
            expiration = new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                    .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli());
        }else {
            expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        }
        var verified = signedJWT.verify(verifier);
        if(!verified && expiration.after(new Date())){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }
}
