package com.example.foodndeliv.repository;

import com.example.foodndeliv.entity.OrderDelivery;
import com.example.foodndeliv.types.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Hypermedia repository for OrderDelivery.
 *
 * Searches are exposed for paging/filtering.
 * DELETE operations are deliberately hidden to preserve
 * immutable delivery history (assignment requirement).
 */
@RepositoryRestResource(
        path = "deliveries",
        collectionResourceRel = "deliveries",
        itemResourceRel = "delivery"
)
public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Long> {

    /* ---------- Search rels (paged + named params) ---------- */

    // GET /api/deliveries/search/by-status?status=ASSIGNED
    @RestResource(path = "by-status", rel = "by-status")
    Page<OrderDelivery> findByStatus(@Param("status") DeliveryStatus status, Pageable pageable);

    // GET /api/deliveries/search/by-rider?riderId=123
    @RestResource(path = "by-rider", rel = "by-rider")
    Page<OrderDelivery> findByRiderId(@Param("riderId") Long riderId, Pageable pageable);

    // GET /api/deliveries/search/by-rider-and-status?riderId=123&status=DELIVERED
    @RestResource(path = "by-rider-and-status", rel = "by-rider-and-status")
    Page<OrderDelivery> findByRiderIdAndStatus(@Param("riderId") Long riderId,
                                               @Param("status") DeliveryStatus status,
                                               Pageable pageable);

    // Optional convenience: GET /api/deliveries/search/by-order-ref?orderRef=ORD-1001
    @RestResource(path = "by-order-ref", rel = "by-order-ref")
    Page<OrderDelivery> findByOrderRef(@Param("orderRef") String orderRef, Pageable pageable);

    /* ---------- Preserve history: hide all delete variants ---------- */
    @Override @RestResource(exported = false)
    void deleteById(Long id);

    @Override @RestResource(exported = false)
    void delete(OrderDelivery entity);

    @Override @RestResource(exported = false)
    void deleteAll();

    // Defensively hide additional delete signatures Spring Data might export
    @Override @RestResource(exported = false)
    void deleteAll(Iterable<? extends OrderDelivery> entities);

    @Override @RestResource(exported = false)
    void deleteAllById(Iterable<? extends Long> ids);
}
