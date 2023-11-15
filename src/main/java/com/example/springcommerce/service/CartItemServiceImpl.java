package com.example.springcommerce.service;

import java.util.*;
import java.util.stream.Collectors;

import com.example.springcommerce.dto.OrderDto;
import com.example.springcommerce.model.Order;
import com.example.springcommerce.model.OrderDetail;
import com.example.springcommerce.product.Product;
import com.example.springcommerce.repository.ProductRepositoryApi;
import com.example.springcommerce.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springcommerce.model.Cart;
import com.example.springcommerce.model.CartItem;
import com.example.springcommerce.repository.CartItemRepository;
import com.example.springcommerce.repository.UserDetailImpRepository;
import com.example.springcommerce.repository.CartRepository;

@Service
public class CartItemServiceImpl {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    UserDetailImpRepository userDetailImpRepository;

    @Autowired
    CartRepository cartRepository;

    public void save(CartItem cartItem) {
        CartItem cartItem2 = new CartItem(cartItem.getProductId(), cartItem.getQuantity());
        // System.out.println("cart 2: " + cartItem2);
        cartItemRepository.save(cartItem2);
    }

    public void updateQuantity(Long id, int quantity) {
        Optional<CartItem> findById = cartItemRepository.findById(id);
        if (findById.isPresent()) {
            findById.get().setQuantity(findById.get().getQuantity() + quantity);
            cartItemRepository.save(findById.get());
        }
    }

    public List<CartItem> getCurrentCarts() {
        return cartItemRepository.findAll();
    }

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    ProductRepositoryApi ProductRepositoryApi;

//    public List<CartItem> getCurrentCartsByCurrentUser() {
//
//        List<Cart> findAll = cartRepository.findAll();
//        String currentUserId = userDetailsServiceImpl.getCurrentUserId();
//        List<CartItem> list = findAll.stream().filter(arg0 -> arg0.getUserId().getUsername().equals(currentUserId))
//                .map(arg0 -> cartItemRepository.findById(arg0.getCartItemId().getId()).get()).toList();
//
//        return list;
//    }


    public List<CartItem> getCurrentCartsByCurrentUser() {
    List<Cart> findAll = cartRepository.findAll();
    String currentUserId = userDetailsServiceImpl.getCurrentUserId();

    List<CartItem> cartItems = findAll.stream()
            .filter(cart -> cart.getUserId().getUsername().equals(currentUserId))
            .map(cart -> {
                CartItem cartItem = cartItemRepository.findById(cart.getCartItemId().getId()).orElse(null);
                if (cartItem != null) {
                    Product product = ProductRepositoryApi.findById(cartItem.getProductId()).orElse(null);
                    if (product != null) {
                        cartItem.setImage(product.getImage());
                        cartItem.setProductName(product.getName());
                        cartItem.setColor(product.getColor());
                    }
                }
                return cartItem;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    return cartItems;
}

    @Transactional
    public void clearCurrentCartByCurrentUser() {
        List<CartItem> currentCartsByCurrentUser = getCurrentCartsByCurrentUser();
        cartRepository.deleteByUserId(userDetailsServiceImpl.getCurrentUser());
        currentCartsByCurrentUser.stream().forEach(
                arg0 -> cartItemRepository.deleteById(arg0.getId()));

    }

    public Double getTotalPrice() {
        List<CartItem> currentCartsByCurrentUser = getCurrentCartsByCurrentUser();

        Double reduce = currentCartsByCurrentUser.stream().map(arg0 -> arg0.getProductPrice() * arg0.getQuantity())
                .reduce(0.0D,
                        (arg0, arg1) -> arg0 + arg1);
        return reduce + 0.0D;
    }

    public void addCartItem(CartItem cartItem) {
        List<CartItem> cartItems = getCurrentCartsByCurrentUser();

        Optional<CartItem> itemOptional = cartItems.stream()
                .filter(item -> item.getProductId().equals(cartItem.getProductId()))
                .findFirst();

        if (itemOptional.isPresent()) {
            updateQuantity(itemOptional.get().getId(), cartItem.getQuantity());
            // CartItem item = itemOptional.get();
            // item.setQuantity(item.getQuantity() + cartItem.getQuantity());
            cartItemRepository.save(itemOptional.get());
        } else {

            CartItem save = cartItemRepository.save(cartItem);
            // System.out.println("create new cart item: " + save);

            Cart cart = new Cart();
            cart.setCartItemId(save);
            cart.setUserId(userDetailsServiceImpl.getCurrentUser());

            // System.out.println("new cart: " + cart);
            cartRepository.save(cart);

            // save(cartItem);
            // cartItems.add(cartItem);
        }
    }
    public int countProductsInCart() {
        String currentUserId = userDetailsServiceImpl.getCurrentUserId();
        List<CartItem> currentCartsByCurrentUser = getCurrentCartsByCurrentUser();

        int totalProducts = currentCartsByCurrentUser.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return totalProducts;
    }

}
