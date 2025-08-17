package com.example.foodndeliv.dto;

import lombok.Builder;
import jakarta.validation.constraints.*;
import lombok.extern.jackson.Jacksonized;

import com.example.foodndeliv.types.*;

@Builder
@Jacksonized
public record CustomerUpdateDTO(
    @Email  @Size(max = 30, message="Email max length exceeded") String email,
    @NotNull(message="Customer state is required") CustomerState state
){}

