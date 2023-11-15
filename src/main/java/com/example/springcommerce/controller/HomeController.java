package com.example.springcommerce.controller;

import com.example.springcommerce.dto.OrderDto;
import com.example.springcommerce.product.Product;
import com.example.springcommerce.product.ProductService;
import com.example.springcommerce.repository.ProductRepositoryApi;
import com.example.springcommerce.service.CartItemServiceImpl;
import com.example.springcommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.springcommerce.service.UserDetailsServiceImpl;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class HomeController {

  @Autowired
  private UserDetailsServiceImpl userDetailsServiceImpl;
  @Autowired
  private ProductRepositoryApi productRepositoryApi;

  @Autowired
  private CartItemServiceImpl cartItemServiceImpl;

  @Autowired
  OrderService orderService;

  @Autowired
  ProductService productService;

  @RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
  public String index(Model model) {
    model.asMap().clear();
    model.addAttribute("isLogin", userDetailsServiceImpl.isLogin());
    model.addAttribute("pageTitle", "Spring Commerce");
    model.addAttribute("isHome", true);
    model.addAttribute("isAdmin", userDetailsServiceImpl.isAdmin());
    model.addAttribute("lastname", userDetailsServiceImpl.getLastname());
    model.addAttribute("firstname", userDetailsServiceImpl.getFirstname());
    model.addAttribute("products", cartItemServiceImpl.countProductsInCart());
    model.addAttribute("total", cartItemServiceImpl.getTotalPrice());
    model.addAttribute("username", userDetailsServiceImpl.getUsername());
    List<OrderDto> orderDtoss = orderService.getOrders();
    model.addAttribute("orders", orderDtoss);
    //
    // Create a new set to store the unique categories
    Set<String> categorySet = new HashSet<>();
    for (Product product :  productRepositoryApi.findAll()) {
      categorySet.add(product.getCategory());
    }
    // Add the categorySet to the model
    model.addAttribute("categorySet", categorySet);
    return "index";
  }



}
