package com.example.foodndeliv.controller;

import com.example.foodndeliv.entity.OrderDelivery;
import com.example.foodndeliv.entity.Rider;
import com.example.foodndeliv.repository.OrderDeliveryRepository;
import com.example.foodndeliv.repository.RiderRepository;
import com.example.foodndeliv.types.DeliveryStatus;
import com.example.foodndeliv.types.RiderStatus;
import com.example.foodndeliv.repository.OrderRepository;
import com.example.foodndeliv.types.RestaurantState;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryCommandController {

  private final OrderDeliveryRepository deliveries;
  private final RiderRepository riders;
  private final OrderRepository orders;

  public DeliveryCommandController(OrderDeliveryRepository deliveries, RiderRepository riders, OrderRepository orders) {
    this.deliveries = deliveries;
    this.riders = riders;
    this.orders = orders;
  }

  public static final class AssignRequest {
    @NotNull @JsonProperty("riderId")
    public Long riderId;
    @NotNull @JsonProperty("orderId")
    public Long orderId;
  }

  public static final class StatusRequest {
    @NotNull @JsonProperty("status")
    public DeliveryStatus status;
  }

  public static final class DeliveryView {
    public Long id;
    public String orderRef;
    public DeliveryStatus status;
    public Long riderId;
    public Instant assignedAt;
    public Instant deliveredAt;
    public Instant updatedAt;

    DeliveryView(Long id, String orderRef, DeliveryStatus status,
                 Long riderId, Instant assignedAt, Instant deliveredAt, Instant updatedAt) {
      this.id = id;
      this.orderRef = orderRef;
      this.status = status;
      this.riderId = riderId;
      this.assignedAt = assignedAt;
      this.deliveredAt = deliveredAt;
      this.updatedAt = updatedAt;
    }
  }

  private static DeliveryView toView(OrderDelivery d) {
    Long riderId = (d.getRider() != null ? d.getRider().getId() : null);
    return new DeliveryView(
        d.getId(), d.getOrderRef(), d.getStatus(),
        riderId, d.getAssignedAt(), d.getDeliveredAt(), d.getUpdatedAt()
    );
  }

  @PatchMapping("/{id}/assign")
  @Transactional
  public ResponseEntity<DeliveryView> assign(@PathVariable Long id, @Valid @RequestBody AssignRequest body) {
    OrderDelivery d = deliveries.findById(id)
        .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Delivery not found"));

    if (d.getStatus() != DeliveryStatus.UNASSIGNED) {
      throw new ApiError(HttpStatus.CONFLICT, "DELIVERY_NOT_UNASSIGNED");
    }

    var order = orders.findById(body.orderId)
     .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND"));
    var restaurant = order.getRestaurant();
    if (restaurant == null) {
      throw new ApiError(HttpStatus.NOT_FOUND, "RESTAURANT_NOT_FOUND");
    }
    if (restaurant.getState() != RestaurantState.OPEN) {
      throw new ApiError(HttpStatus.CONFLICT, "RESTAURANT_CLOSED");
    }

    Rider r = riders.findById(body.riderId)
        .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Rider not found"));

    if (r.getStatus() == RiderStatus.INACTIVE) {
      throw new ApiError(HttpStatus.CONFLICT, "RIDER_INACTIVE");
    }

    d.setRider(r);
    d.setStatus(DeliveryStatus.ASSIGNED);
    if (d.getAssignedAt() == null) d.setAssignedAt(Instant.now());

    OrderDelivery saved = deliveries.save(d);
    return ResponseEntity.ok(toView(saved));
  }

  @PatchMapping(value = "/{id}/assign", params = { "riderId", "orderId" })
  @Transactional
  public ResponseEntity<DeliveryView> assignWithParam(@PathVariable Long id, @RequestParam Long riderId, @RequestParam Long orderId) {
    AssignRequest req = new AssignRequest();
    req.riderId = riderId;
    req.orderId = orderId;
    return assign(id, req);
  }

  @PatchMapping("/{id}/status")
  @Transactional
  public ResponseEntity<DeliveryView> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest body) {
    OrderDelivery d = deliveries.findById(id)
        .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "Delivery not found"));

    DeliveryStatus target = body.status;

    if (target == DeliveryStatus.UNASSIGNED) {
      throw new ApiError(HttpStatus.CONFLICT, "CANNOT_SET_UNASSIGNED");
    }

    if (target == DeliveryStatus.DELIVERED) {
      if (d.getStatus() != DeliveryStatus.ASSIGNED || d.getRider() == null) {
        throw new ApiError(HttpStatus.CONFLICT, "DELIVERY_NOT_ASSIGNED");
      }
      d.setStatus(DeliveryStatus.DELIVERED);
      if (d.getDeliveredAt() == null) d.setDeliveredAt(Instant.now());
      OrderDelivery saved = deliveries.save(d);
      return ResponseEntity.ok(toView(saved));
    }

    throw new ApiError(HttpStatus.BAD_REQUEST, "Unsupported status: " + target);
  }

  static final class ApiError extends RuntimeException {
    private final HttpStatus status;
    ApiError(HttpStatus status, String message) { super(message); this.status = status; }
    HttpStatus getStatus() { return status; }
  }

  @ExceptionHandler(ApiError.class)
  public ResponseEntity<Object> handleApiError(ApiError e) {
    return ResponseEntity
        .status(e.getStatus())
        .body(Map.of("error", e.getMessage(), "status", e.getStatus().value()));
  }

  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<Object> handleStale(ObjectOptimisticLockingFailureException e) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(Map.of("error", "STALE_UPDATE", "status", HttpStatus.CONFLICT.value()));
  }
}
