package com.devteria.identity_service.dto.request;

import com.devteria.identity_service.validator.DobConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String userId;
    String password;
    String firstName;
    String lastName;
    @DobConstraint(min = 17, message = "INVALID_BIRTHDATE")
    LocalDate dob;
    Set<String> roles;
}
