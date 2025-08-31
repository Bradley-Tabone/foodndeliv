package com.example.foodndeliv.entity;

import com.example.foodndeliv.types.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Check;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Check(constraints =
  "(status = 'UNASSIGNED' AND rider_id IS NULL) OR " +
  "(status IN ('ASSIGNED','DELIVERED') AND rider_id IS NOT NULL)"
)

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

    @NotBlank
    @Column(name = "order_ref", nullable = false, length = 64)
    private String orderRef;

    @Size(max = 256)
    @Column(name = "pickup_address", length = 256)
    private String pickupAddress;

    @Size(max = 256)
    @Column(name = "dropoff_address", length = 256)
    private String dropoffAddress;

    @DecimalMin(value = "0.00")
    @Digits(integer = 8, fraction = 2)
    @Column(name = "fee", precision = 10, scale = 2)
    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private DeliveryStatus status = DeliveryStatus.UNASSIGNED;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "rider_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Rider rider;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    public OrderDelivery() {}

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

    public void setStatus(DeliveryStatus status) { this.status = status; }

    public Rider getRider() { return rider; }

    public void setRider(Rider rider) { this.rider = rider; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }

    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }

    public long getVersion() { return version; }
}
