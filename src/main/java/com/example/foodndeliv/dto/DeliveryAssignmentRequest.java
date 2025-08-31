package com.example.foodndeliv.dto;

import com.example.foodndeliv.types.DeliveryStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public class DeliveryAssignmentRequest {

    private Long riderId;
    @NotNull
    private DeliveryStatus status;

    public Long getRiderId() { return riderId; }
    public void setRiderId(Long riderId) { this.riderId = riderId; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }

    @AssertTrue(message = "When status is ASSIGNED, riderId is required; when status is DELIVERED, riderId must be absent.")
    public boolean isCombinationValid() {
        if (status == null) return false;
        switch (status) {
            case ASSIGNED:
                return riderId != null;
            case DELIVERED:
                return riderId == null;
            case UNASSIGNED:
                return false;
            default:
                return false;
        }
    }
}
