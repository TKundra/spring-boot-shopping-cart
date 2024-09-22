package com.backend.shoppingcart.service.cart;

import com.backend.shoppingcart.model.Cart;
import com.backend.shoppingcart.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);
    Cart initializeNewCart(User user);
    Cart getCartByUserId(Long userId);

    BigDecimal getTotalPrice(Long id);

    void clearCart(Long id);
}
