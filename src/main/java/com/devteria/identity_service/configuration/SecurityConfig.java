package com.devteria.identity_service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // Danh sách các API không cần xác thực (Public)
    private final String[] PUBLIC_ENDPOINTS = {
            "/users", "/auth/login", "/auth/verify", "/auth/logout", "/auth/refresh"
    };
    private final String[] PRIVATE_ENDPOINTS = {
            "/users"
    };

    @Autowired
    CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll() // Cho phép các API công khai
//                        .requestMatchers(HttpMethod.GET, PRIVATE_ENDPOINTS).hasAuthority("ROLE_ADMIN") // Chỉ cho phép người dùng có Role USER truy cập API GET /users
                        .requestMatchers(HttpMethod.DELETE, PRIVATE_ENDPOINTS).hasAuthority("ROLE_ADMIN") // Chỉ cho phép người dùng có Role USER truy cập API DELETE /users
                        .anyRequest().authenticated()); // Tất cả các yêu cầu còn lại phải có Token

        // Cấu hình để ứng dụng đóng vai trò là Resource Server nhận JWT
        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                       .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );

        // Tắt CSRF vì chúng ta dùng Token, không dùng Session (Stateless)
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() { // Cấu hình để lấy thông tin Role từ claim "roles" trong JWT
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
