package com.example.foodndeliv.dto;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import jakarta.validation.constraints.*;

@Builder
@Jacksonized
public record CustomerRequestDTO(
        @NotBlank(message="Name cannot be blank") @Size(max = 30, message="Name max length exceeded") @Pattern(regexp="^[a-zA-Z0-9\\s]*$", message="Invalid Name") String name,
        @Email  @Size(max = 30, message="Email max length exceeded") String email
){}

