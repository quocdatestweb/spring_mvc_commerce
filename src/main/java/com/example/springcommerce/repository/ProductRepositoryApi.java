package com.example.springcommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.example.springcommerce.product.Product;

@Component
public interface ProductRepositoryApi extends JpaRepository<Product, Long> {
    List<Product> findProductsByOrdersId(Long orderId);
    List<Product> findByCategory(String category);

    List<Product> findByColor(String color);


    List<Product> findByNameContaining(String name);
}
