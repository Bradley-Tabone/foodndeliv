package com.example.foodndeliv.controller;

import com.example.foodndeliv.dto.*;
import com.example.foodndeliv.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/orders")  // <-- match KrakenD config
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO createOrder(@RequestBody @Valid OrderRequestDTO orderRequestDTO) {
        System.out.println("createOrder called");
        return orderService.createOrder(orderRequestDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponseDTO> getAllOrders() {
        System.out.println("getAllOrders called");
        return orderService.getAllOrders();
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponseDTO> getOrdersByUsername(@PathVariable("username") String username) {
        return orderService.getOrdersByUsername(username);
    }
}
