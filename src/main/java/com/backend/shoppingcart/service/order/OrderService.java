package com.backend.shoppingcart.service.order;

import com.backend.shoppingcart.dto.OrderDto;
import com.backend.shoppingcart.enums.OrderStatus;
import com.backend.shoppingcart.exception.ResourceNotFoundException;
import com.backend.shoppingcart.model.Cart;
import com.backend.shoppingcart.model.Order;
import com.backend.shoppingcart.model.OrderItem;
import com.backend.shoppingcart.model.Product;
import com.backend.shoppingcart.repository.OrderRepository;
import com.backend.shoppingcart.repository.ProductRepository;
import com.backend.shoppingcart.service.cart.ICartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ICartService cartService;

    /* --------------------------- private methods --------------------------- */
    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems
                .stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItem> createOrderItem(Order order, Cart cart) {
        return cart.getCartItems()
                .stream()
                .map(cartItem -> {
                    // update inventory
                    Product product = cartItem.getProduct();
                    product.setInventory(product.getInventory() - cartItem.getQuantity());
                    productRepository.save(product);

                    // return order item
                    return new OrderItem(
                            order,
                            product,
                            cartItem.getQuantity(),
                            cartItem.getUnitPrice()
                    );
                }).collect(Collectors.toList());
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }

    /* --------------------------- private methods end --------------------------- */

    @Transactional
    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        Order order = createOrder(cart);

        List<OrderItem> orderItems = createOrderItem(order, cart);

        // pass order items & set total amount
        order.setOrderItems(new HashSet<>(orderItems));
        order.setTotalAmount(calculateTotalAmount(orderItems));

        // save order to db
        Order savedOrder = orderRepository.save(order);

        // clear the cart after saving the order
        cartService.clearCart(cart.getId());

        return savedOrder;
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }
}
