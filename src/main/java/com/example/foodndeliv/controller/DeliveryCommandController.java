package com.example.foodndeliv.controller;

import com.example.foodndeliv.dto.DeliveryAssignmentRequest;
import com.example.foodndeliv.entity.OrderDelivery;
import com.example.foodndeliv.entity.Rider;
import com.example.foodndeliv.repository.OrderDeliveryRepository;
import com.example.foodndeliv.repository.RiderRepository;
import com.example.foodndeliv.types.DeliveryStatus;
import com.example.foodndeliv.types.RiderStatus;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RepositoryRestController
public class DeliveryCommandController {

    private final OrderDeliveryRepository deliveryRepo;
    private final RiderRepository riderRepo;

    @Autowired
    public DeliveryCommandController(OrderDeliveryRepository deliveryRepo, RiderRepository riderRepo) {
        this.deliveryRepo = deliveryRepo;
        this.riderRepo = riderRepo;
    }

    /**
     * Command endpoint to drive delivery lifecycle with invariants:
     *  - UNASSIGNED -> ASSIGNED (requires ACTIVE rider)
     *  - ASSIGNED   -> DELIVERED
     *
     * Full path at runtime: POST /api/deliveries/{id}/assign
     * NOTE: We do NOT allow setting UNASSIGNED here to preserve traceability.
     */
    @PostMapping(path = "/deliveries/{id}/assign", consumes = "application/json")
    @Transactional
    public ResponseEntity<PersistentEntityResource> assign(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryAssignmentRequest req,
            PersistentEntityResourceAssembler assembler) {

        OrderDelivery d = deliveryRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found"));

        DeliveryStatus current = d.getStatus();
        DeliveryStatus target = req.getStatus();

        switch (target) {
            case ASSIGNED -> {
                // Only allow from UNASSIGNED
                if (current != DeliveryStatus.UNASSIGNED) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "Only UNASSIGNED deliveries can be assigned");
                }
                Long riderId = req.getRiderId();
                if (riderId == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "riderId is required when status=ASSIGNED");
                }
                Rider r = riderRepo.findById(riderId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Rider not found"));
                if (r.getStatus() == RiderStatus.INACTIVE) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "INACTIVE riders cannot be assigned to deliveries");
                }

                d.setRider(r);
                d.setStatus(DeliveryStatus.ASSIGNED);
                // business timestamp if your entity includes it
                try { d.setAssignedAt(Instant.now()); } catch (NoSuchMethodError | Exception ignored) {}
            }
            case DELIVERED -> {
                // Only allow from ASSIGNED
                if (current != DeliveryStatus.ASSIGNED) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "Only ASSIGNED deliveries can be marked DELIVERED");
                }
                d.setStatus(DeliveryStatus.DELIVERED);
                // business timestamp if your entity includes it
                try { d.setDeliveredAt(Instant.now()); } catch (NoSuchMethodError | Exception ignored) {}
            }
            case UNASSIGNED -> {
                // Disallow making things UNASSIGNED via this endpoint
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Setting UNASSIGNED is not supported via this command");
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported status: " + target);
        }

        OrderDelivery saved = deliveryRepo.save(d);
        return ResponseEntity.ok(assembler.toModel(saved)); // 200 OK + HAL body
    }
}
