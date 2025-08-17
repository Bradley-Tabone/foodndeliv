package com.example.foodndeliv.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

import com.example.foodndeliv.types.OrderState;

@Data
public class OrderResponseDTO{ 
    @NotNull @PositiveOrZero Long id;
    @NotNull(message="Missing customer") CustomerDTO customer;
    @NotNull(message="Missing restaurant") RestaurantDTO restaurant;
    @Size(max = 500, message="Order details max length exceeded") @Pattern(regexp="^[a-zA-Z0-9\\s]*$", message="Invalid Order details") String orderDetails;
    @NotEmpty(message="At least one order line is required") List<OrderLineDTO> orderLines;
    @NotNull OrderState state;
    @PositiveOrZero(message="Price >=0") Double totalPrice;
}

