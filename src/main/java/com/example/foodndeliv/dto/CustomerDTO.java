package com.example.foodndeliv.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import com.example.foodndeliv.types.*;

@Data
public class CustomerDTO{
        @NotNull @PositiveOrZero Long id;
        @NotBlank(message="Name cannot be blank") @Size(max = 30, message="Name max length exceeded") @Pattern(regexp="^[a-zA-Z0-9\\s]*$", message="Invalid Name") String name;
        @Email  @Size(max = 30, message="Email max length exceeded") String email;
        @NotNull(message="Customer state is required") CustomerState state;
}
