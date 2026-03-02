package com.devteria.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {
    @Id
    String rolename;
    String description;
    @ManyToMany
    Set<Permission> permissions;
    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime creDate;
    @UpdateTimestamp
    LocalDateTime updDate;
    String createdBy;
    String updatedBy;
}
