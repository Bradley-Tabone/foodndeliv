package com.example.foodndeliv.entity;

import com.example.foodndeliv.repository.RiderRepository;
import com.example.foodndeliv.types.RiderStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/riders")
public class RiderCommands {

    private final RiderRepository riders;

    public RiderCommands(RiderRepository riders) {
        this.riders = riders;
    }

    /** DTO for changing rider status */
    public record ChangeRiderStatusDto(String status) { }

    /**
     * Explicit command to change a rider's status.
     * Example:
     *   PATCH /api/riders/7/status
     *   { "status": "INACTIVE" }
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Long id,
                                          @RequestBody ChangeRiderStatusDto body) {
        if (body == null || body.status() == null || body.status().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Field 'status' is required (ACTIVE or INACTIVE)."));
        }

        final RiderStatus newStatus;
        try {
            newStatus = RiderStatus.valueOf(body.status().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid status. Allowed: ACTIVE, INACTIVE"));
        }

        var riderOpt = riders.findById(id);
        if (riderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var rider = riderOpt.get();
        rider.setStatus(newStatus); // package‑private setter on Rider
        riders.save(rider);

        return ResponseEntity.noContent().build();
    }
}
