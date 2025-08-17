package com.example.foodndeliv.entity;

import com.example.foodndeliv.types.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
    name = "deliveries",
    indexes = {
        @Index(name = "ix_deliveries_status", columnList = "status"),
        @Index(name = "ix_deliveries_rider_id", columnList = "rider_id"),
        @Index(name = "ix_deliveries_order_ref", columnList = "order_ref")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class OrderDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Reference to an order in the wider system (free-form for Task 1). */
    @NotBlank
    @Column(name = "order_ref", nullable = false, length = 64)
    private String orderRef;

    @Size(max = 256)
    @Column(name = "pickup_address", length = 256)
    private String pickupAddress;

    @Size(max = 256)
    @Column(name = "dropoff_address", length = 256)
    private String dropoffAddress;

    /** Optional rider fee charged for this delivery. */
    @DecimalMin(value = "0.00")
    @Digits(integer = 8, fraction = 2)
    @Column(name = "fee", precision = 10, scale = 2)
    private BigDecimal fee;

    /** Domain state (guarded as read-only over JSON; change via command endpoints). */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private DeliveryStatus status = DeliveryStatus.UNASSIGNED;

    /**
     * Association is READ-ONLY over JSON; use /api/deliveries/{id}/assign to change.
     * Use LAZY to avoid n+1 and heavy HAL payloads.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "rider_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Rider rider;

    /** Timestamps for traceability */
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Business timestamps for lifecycle */
    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    /** Optional optimistic lock to prevent concurrent overwrites */
    @Version
    @Column(name = "version", nullable = false)
    private long version;

    public OrderDelivery() {}

    // Getters / Setters

    public Long getId() { return id; }

    public String getOrderRef() { return orderRef; }
    public void setOrderRef(String orderRef) { this.orderRef = orderRef; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDropoffAddress() { return dropoffAddress; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public DeliveryStatus getStatus() { return status; }

    /**
     * Intentionally package-private to discourage direct mutation outside command handlers.
     * Changes should happen via your DeliveryCommandController (assign / status endpoints).
     */
    public void setStatus(DeliveryStatus status) { this.status = status; }

    public Rider getRider() { return rider; }

    /** Same rationale as setStatus: command endpoint should manage this. */
    public void setRider(Rider rider) { this.rider = rider; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }

    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }

    public long getVersion() { return version; }
}
