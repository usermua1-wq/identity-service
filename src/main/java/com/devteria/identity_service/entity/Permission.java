package com.devteria.identity_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission {
    @Id
    String permissionname;
    String description;
    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime creDate;
    @UpdateTimestamp
    LocalDateTime updDate;
    String createdBy;
    String updatedBy;
}
