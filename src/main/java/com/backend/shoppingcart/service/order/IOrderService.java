package com.backend.shoppingcart.service.order;

import com.backend.shoppingcart.dto.OrderDto;
import com.backend.shoppingcart.model.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);
    List<OrderDto> getUserOrders(Long userId);

    OrderDto convertToDto(Order order);
}
