package com.example.foodndeliv.dto;

import lombok.Builder;
import lombok.Singular;
import jakarta.validation.constraints.*;
import jakarta.validation.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
public record OrderRequestDTO(
    @NotNull(message="Customer missing") @PositiveOrZero(message="Invalid customer") Long customerId,
    @NotNull(message="Customer missing") @PositiveOrZero(message="Invalid restaurant") Long restaurantId,
    @Size(max = 500, message="Order details max length exceeded") @Pattern(regexp="^[a-zA-Z0-9\\s]*$", message="Invalid details") String orderDetails,
    @NotEmpty(message="At least one order line is required") @Singular @Valid List<OrderLineDTO>  orderLines
){}
