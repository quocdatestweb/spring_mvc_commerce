package com.example.springcommerce.api;

import com.example.springcommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deletecart")
public class CartControllerApi {

    private final CartService cartService;

    @Autowired
    public CartControllerApi(CartService cartService) {
        this.cartService = cartService;
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> deleteCartById(@PathVariable("cartId") Long cartId) {
        cartService.deleteCartById(cartId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}