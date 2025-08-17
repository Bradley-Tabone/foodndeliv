package com.example.foodndeliv.entity;

import com.example.foodndeliv.types.RiderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(
    name = "riders",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_riders_phone", columnNames = {"phone"})
    },
    indexes = {
        @Index(name = "ix_riders_status", columnList = "status"),
        @Index(name = "ix_riders_phone", columnList = "phone")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Length(max = 128)
    @Column(name = "full_name", nullable = false, length = 128)
    private String fullName;

    // Keep short for Maltese numbers; adjust if you expect international formats
    @NotBlank
    @Length(max = 32)
    @Pattern(regexp = "^[+0-9][0-9\\s-]{5,31}$",
             message = "Phone must be digits, spaces or dashes and may start with +")
    @Column(name = "phone", nullable = false, unique = true, length = 32)
    private String phone;

    /**
     * Guard status from being flipped via repository PUT/PATCH.
     * Make it JSON read-only and restrict setter visibility.
     * Use a command endpoint/DTO to change it (e.g., /api/riders/{id}/status).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private RiderStatus status = RiderStatus.ACTIVE;

    /** Auditing / traceability */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Prevent silent clobbering on concurrent edits */
    @Version
    @Column(name = "version", nullable = false)
    private long version;

    public Rider() {}

    public Rider(String fullName, String phone, RiderStatus status) {
        this.fullName = fullName;
        this.phone = phone;
        this.status = status == null ? RiderStatus.ACTIVE : status;
    }

    // Getters / Setters

    public Long getId() { return id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public RiderStatus getStatus() { return status; }

    /** Package-private: only your service/command layer in same package should call this. */
    void setStatus(RiderStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public long getVersion() { return version; }
}
