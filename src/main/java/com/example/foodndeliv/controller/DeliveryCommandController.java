package com.example.foodndeliv.controller;

import com.example.foodndeliv.entity.OrderDelivery;
import com.example.foodndeliv.repository.OrderDeliveryRepository;
import com.example.foodndeliv.repository.RiderRepository;
import com.example.foodndeliv.types.DeliveryStatus;
import com.example.foodndeliv.types.RiderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

/**
 * Command-style endpoints (separate from the Spring Data REST CRUD) to enforce invariants:
 *  - INACTIVE riders cannot be assigned to deliveries
 *  - Delivery history is preserved (no command to set UNASSIGNED or to delete)
 *
 * Paths are explicit under /api to make the contract clear and easy to test with curl/Postman.
 */
@RestController
@RequestMapping("/api/deliveries")
@Validated
public class DeliveryCommandController {

    private final OrderDeliveryRepository deliveries;
    private final RiderRepository riders;

    public DeliveryCommandController(OrderDeliveryRepository deliveries, RiderRepository riders) {
        this.deliveries = deliveries;
        this.riders = riders;
    }

    // ---------- DTOs ----------

    public static final class AssignRequest {
        @NotNull
        @JsonProperty("riderId")
        public Long riderId;
    }

    public static final class StatusRequest {
        @NotNull
        @JsonProperty("status")
        public DeliveryStatus status;
    }

    // ---------- Commands ----------

    /**
     * Assign a delivery to an ACTIVE rider.
     * Allowed only from UNASSIGNED -> ASSIGNED.
     *
     * Example:
     *   PATCH /api/deliveries/{id}/assign
     *   { "riderId": 8 }
     */
    @PatchMapping("/{id}/assign")
    @Transactional
    public ResponseEntity<?> assign(@PathVariable Long id, @Valid @RequestBody AssignRequest body) {
        OrderDelivery d = deliveries.findById(id)
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Delivery not found"));

        if (d.getStatus() != DeliveryStatus.UNASSIGNED) {
            throw new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Only UNASSIGNED deliveries can be assigned");
        }

        var rider = riders.findById(body.riderId)
                .orElseThrow(() -> new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, "Rider not found"));

        if (rider.getStatus() == RiderStatus.INACTIVE) {
            // Business conflict: rider exists but is not eligible
            throw new ApiError(HttpStatus.CONFLICT, "Cannot assign to INACTIVE rider");
        }

        d.setRider(rider);
        d.setStatus(DeliveryStatus.ASSIGNED);
        if (d.getAssignedAt() == null) {
            d.setAssignedAt(Instant.now());
        }

        deliveries.save(d);
        return ResponseEntity.noContent().build(); // 204
    }

    /**
     * Convenience variant using query param if you ever want to bypass JSON binding:
     *   PATCH /api/deliveries/{id}/assign?riderId=8
     */
    @PatchMapping(value = "/{id}/assign", params = "riderId")
    @Transactional
    public ResponseEntity<?> assignWithParam(@PathVariable Long id, @RequestParam Long riderId) {
        AssignRequest req = new AssignRequest();
        req.riderId = riderId;
        return assign(id, req);
    }

    /**
     * Update delivery status.
     * Allowed transition:
     *   ASSIGNED -> DELIVERED (sets deliveredAt)
     * Disallowed through this command:
     *   any attempt to set UNASSIGNED (history must be preserved)
     *
     * Example:
     *   PATCH /api/deliveries/{id}/status
     *   { "status": "DELIVERED" }
     */
    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest body) {
        OrderDelivery d = deliveries.findById(id)
                .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Delivery not found"));

        DeliveryStatus target = body.status;

        if (target == DeliveryStatus.UNASSIGNED) {
            throw new ApiError(HttpStatus.UNPROCESSABLE_ENTITY,
                    "UNASSIGNED cannot be set via command; history must be preserved");
        }

        if (target == DeliveryStatus.DELIVERED) {
            if (d.getStatus() != DeliveryStatus.ASSIGNED) {
                throw new ApiError(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Only ASSIGNED deliveries can be marked DELIVERED");
            }
            d.setStatus(DeliveryStatus.DELIVERED);
            if (d.getDeliveredAt() == null) {
                d.setDeliveredAt(Instant.now());
            }
            deliveries.save(d);
            return ResponseEntity.noContent().build(); // 204
        }

        // If we reach here, the requested status is not supported via this command
        throw new ApiError(HttpStatus.BAD_REQUEST, "Unsupported status transition: " + target);
    }

    // ---------- Simple typed exception for concise rule errors ----------

    @ResponseStatus
    static class ApiError extends RuntimeException {
        private final HttpStatus status;

        ApiError(HttpStatus status, String message) {
            super(message);
            this.status = status;
        }

        public HttpStatus getStatus() {
            return status;
        }
    }

    // Optionally, turn ApiError into proper HTTP responses without a full @ControllerAdvice:
    @ExceptionHandler(ApiError.class)
    public ResponseEntity<?> handleApiError(ApiError e) {
        return ResponseEntity.status(e.getStatus())
                .body(e.getMessage());
    }
}
