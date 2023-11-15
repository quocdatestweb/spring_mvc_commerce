package com.example.springcommerce.repository;

import com.example.springcommerce.model.Cart;
import com.example.springcommerce.model.UserDetailsImp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findOrdersByUserId(Long userId); // Update method name to 'findOrdersByUserId'

    void deleteByUserId(UserDetailsImp currentUser);
}