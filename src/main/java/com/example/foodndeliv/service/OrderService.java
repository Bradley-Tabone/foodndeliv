package com.example.foodndeliv.service;

import com.example.foodndeliv.repository.*;
import com.example.foodndeliv.types.CustomerState;
import com.example.foodndeliv.types.OrderState;
import com.example.foodndeliv.types.RestaurantState;

import jakarta.servlet.http.HttpServletRequest;

import com.example.foodndeliv.dto.*;
import com.example.foodndeliv.entity.*;
import com.example.foodndeliv.exceptions.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HttpServletRequest request;


    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        

        String custId = request.getHeader("X-Custid");

        //Test
        System.out.println("CreateOrder Custid: "+custId);

        if(custId != null)
        {
                //Check JWT ID matches Oder
                if(custId == null || !custId.equals(orderRequestDTO.customerId().toString())) 
                {
                        throw new DomainInvariantException("Only orders on own behalf are accepted");
                }
        } else {
                // Halt due to missing JWT id
                throw new DomainInvariantException("Only orders on own behalf are accepted");
        }
       
              
        Customer customer = customerRepository.findById(orderRequestDTO.customerId())
                .orElseThrow(() -> new DomainInvariantException("Customer not found"));
        Restaurant restaurant = restaurantRepository.findById(orderRequestDTO.restaurantId())
                .orElseThrow(() -> new DomainInvariantException("Restaurant not found"));

        if( customer.getState() != CustomerState.ACTIVE ) {
                throw new DomainInvariantException("Inactive Customer");
        } else if (restaurant.getState() != RestaurantState.OPEN) {
                throw new DomainInvariantException("Restaurant is currently closed");
        }
        
        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setState(OrderState.OPEN);

        List<OrderLine> orderLines = new ArrayList<>();

        for(OrderLineDTO orderlineto : orderRequestDTO.orderLines())
        {
                orderLines.add(modelMapper.map(orderlineto, OrderLine.class));
        }

        orderLines.forEach(orderLine -> orderLine.setOrder(order));
        order.setOrderLines(orderLines);

        Order newOrder = orderRepository.save(order);

        return modelMapper.map(newOrder, OrderResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {

        List<OrderResponseDTO> retOrders = new ArrayList<>();

        for(Order order :orderRepository.findAll())
        {
                retOrders.add(modelMapper.map(order, OrderResponseDTO.class));
        }
        return retOrders;
    }
}
