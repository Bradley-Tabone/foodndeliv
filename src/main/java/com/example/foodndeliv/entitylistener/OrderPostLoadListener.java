package com.example.foodndeliv.entitylistener;

import jakarta.persistence.PostLoad;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.foodndeliv.dto.*;
import com.example.foodndeliv.entity.Order;

@Component
public class OrderPostLoadListener {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Validator validator;


    @PostLoad
    public void handlePostLoad(Order order) {

        OrderResponseDTO orderDTO = modelMapper.map(order, OrderResponseDTO.class);

        validateOrderDTO(orderDTO);
    }

    private void validateOrderDTO(OrderResponseDTO orderResponseDTO) {

        Set<ConstraintViolation<OrderResponseDTO>> violations = validator.validate(orderResponseDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Order DTO validation failed", violations);
        }
    }
}

