package com.devteria.identity_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class BaseEntity {
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creDate;

    @UpdateTimestamp
    private LocalDateTime updDate;

    private String createdBy;

    private String updatedBy;
}
