package com.backend.shoppingcart.service.cart;

import com.backend.shoppingcart.exception.ResourceNotFoundException;
import com.backend.shoppingcart.model.Cart;
import com.backend.shoppingcart.model.CartItem;
import com.backend.shoppingcart.model.Product;
import com.backend.shoppingcart.repository.CartItemRepository;
import com.backend.shoppingcart.repository.CartRepository;
import com.backend.shoppingcart.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class CartItemService implements ICartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ICartService cartService;
    @Autowired
    private IProductService productService;

    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        /*
        * Get the Cart
        * Get the product
        * Check if the product already in the cart
        * If Yes - then increase the quantity with the requested quantity
        * If No  - then initiate a new CartItem entry
        * */
        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);

        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(new CartItem());

        if (cartItem.getId() == null) {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItem.setTotalPrice();
        cart.addCartItem(cartItem);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        return cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        CartItem itemToRemove = getCartItem(cartId, productId);
        cart.removeItem(itemToRemove);
        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);

        cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(cartItem -> {
                    cartItem.setQuantity(quantity);
                    cartItem.setUnitPrice(cartItem.getProduct().getPrice());
                    cartItem.setTotalPrice();
                });

        BigDecimal totalAmount = cart.getCartItems()
                .stream().map(CartItem ::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }


}
