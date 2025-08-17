package com.example.foodndeliv.controller;

import com.example.foodndeliv.service.*;
import com.example.foodndeliv.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.*;

@RestController
@RequestMapping("/api/ctrl/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDTO createCustomer(@RequestBody @Valid CustomerRequestDTO customerRequestDTO) {
        return customerService.createCustomer(customerRequestDTO);
    }
    
    
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponseDTO updateCustomer(@PathVariable Long id, @RequestBody @Valid CustomerUpdateDTO customerUpdateDTO) {
        System.out.println("updateCustomer called");
        return customerService.updateCustomer(id, customerUpdateDTO);
    }
    
}
