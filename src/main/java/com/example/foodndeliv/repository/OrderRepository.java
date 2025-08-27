package com.example.foodndeliv.repository;

import com.example.foodndeliv.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RepositoryRestResource(path = "orders")
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Search by order details (contains text)
    List<Order> findByOrderDetailsContaining(String orderDetails);

    // Search orders by restaurant name
    @Query("SELECT o FROM Order o WHERE o.restaurant.name = :restaurantName")
    List<Order> findOrdersByRestaurantName(@Param("restaurantName") String restaurantName);

    // Search orders by customer ID
    @Query("SELECT o FROM Order o WHERE o.customer.id = :custID")
    List<Order> findOrdersByCustID(@Param("custID") Long custID);

    // Search orders by customer + restaurant ID
    @Query("SELECT o FROM Order o WHERE o.customer.id = :custID AND o.restaurant.id = :restID")
    List<Order> findOrdersByCustRestID(@Param("custID") Long custID, @Param("restID") Long restID);

    @RestResource(path = "by-customer-name", rel = "by-customer-name")
    @Query("SELECT o FROM Order o WHERE o.customer.name = :name")
    Page<Order> findByCustomerName(@Param("name") String name, Pageable pageable);


    // Prevent DELETE via REST
    @Override
    @RestResource(exported = false)
    default void deleteById(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }
}
