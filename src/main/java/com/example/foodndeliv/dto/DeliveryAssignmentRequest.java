package com.example.foodndeliv.dto;

import com.example.foodndeliv.types.DeliveryStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

/**
 * Command DTO for delivery lifecycle operations.
 *
 * Usage recommendations:
 *  - For ASSIGNED: provide riderId (required).
 *  - For DELIVERED: riderId must be absent (delivery must already be ASSIGNED).
 * 
 * NOTE: We intentionally do NOT allow clients to set UNASSIGNED via this DTO to avoid
 * bypassing the controlled state machine (UNASSIGNED should be initial state on creation).
 * If you really need to support unassigning, expose a dedicated endpoint with audit logging.
 */
public class DeliveryAssignmentRequest {

    private Long riderId;               // required when status==ASSIGNED
    @NotNull
    private DeliveryStatus status;      // allowed: ASSIGNED, DELIVERED

    public Long getRiderId() { return riderId; }
    public void setRiderId(Long riderId) { this.riderId = riderId; }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }

    /** Field-combo validation (runs before controller logic) */
    @AssertTrue(message = "When status is ASSIGNED, riderId is required; when status is DELIVERED, riderId must be absent.")
    public boolean isCombinationValid() {
        if (status == null) return false;
        switch (status) {
            case ASSIGNED:
                return riderId != null;
            case DELIVERED:
                return riderId == null;
            case UNASSIGNED:
                // disallow via this command to preserve controlled lifecycle
                return false;
            default:
                return false;
        }
    }
}
