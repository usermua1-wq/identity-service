package com.devteria.identity_service.dto.response;

import com.devteria.identity_service.entity.Permission;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleDTO {
        String rolename;
        String description;
        Set<PermissionDTO> permissions;
}
