package com.example.foodndeliv.dto;

import lombok.*;

import jakarta.validation.constraints.*;


@Data
public class OrderLineDTO{
    @NotBlank(message="Product cannot be blank") @Size(max = 30, message="Product max length exceeded") @Pattern(regexp="^[a-zA-Z0-9\\s]*$", message="Invalid Product") String productName;
    @NotNull @PositiveOrZero(message="Quantity >=0") Integer quantity;
    @NotNull(message="Line price is missing") @PositiveOrZero(message="Price >=0")Double price;
}
 