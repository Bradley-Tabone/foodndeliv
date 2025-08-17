package com.example.foodndeliv.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import com.example.foodndeliv.types.*;

@Data
public class RestaurantDTO{
    @NotNull @PositiveOrZero Long id;
    @NotBlank(message="Name cannot be blank") @Size(max = 30, message="Name max length exceeded")  @Pattern(regexp="^[a-zA-Z0-9\\s]*$", message="Invalid Name") String name;
    @Size(max = 60, message="Name max length exceeded") @Pattern(regexp="^[a-zA-Z0-9\\s]*$", message="Invalid Address") String address;
    @NotNull RestaurantState state;
}
