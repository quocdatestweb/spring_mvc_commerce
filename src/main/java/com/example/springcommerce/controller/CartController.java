package com.example.springcommerce.controller;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.example.springcommerce.dto.OrderDto;
import com.example.springcommerce.repository.CartItemRepository;
import com.example.springcommerce.repository.ProductRepositoryApi;
import com.example.springcommerce.service.OrderService;
import com.example.springcommerce.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.springcommerce.model.CartItem;
import com.example.springcommerce.service.CartItemServiceImpl;
import com.example.springcommerce.service.UserDetailsServiceImpl;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

  @Autowired
  private UserDetailsServiceImpl userDetailsServiceImpl;

  @Autowired
  CartItemServiceImpl cartItemServiceImpl;
  @Autowired
  com.example.springcommerce.repository.ProductRepositoryApi ProductRepositoryApi;
  @Autowired
  OrderService orderService;


  @RequestMapping(value = { "" }, method = RequestMethod.GET)
  public String viewCart(Model model) {
    model.addAttribute("isCart", true);
    model.addAttribute("isLogin", userDetailsServiceImpl.isLogin());
    model.addAttribute("pageTitle", "Shopping Cart");
    model.addAttribute("isAdmin", userDetailsServiceImpl.isAdmin());
    model.addAttribute("lastname", userDetailsServiceImpl.getLastname());
    model.addAttribute("firstname", userDetailsServiceImpl.getFirstname());
    List<CartItem> carts = cartItemServiceImpl.getCurrentCartsByCurrentUser();
    Double totalPrice = cartItemServiceImpl.getTotalPrice();
    model.addAttribute("currentUser", userDetailsServiceImpl.getCurrentUser());
    model.addAttribute("cartId", ThreadLocalRandom.current().nextInt(1000, 10000));
    model.addAttribute("cartDate", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDate.now()));
    model.addAttribute("carts", carts);
    model.addAttribute("subTotalPrice", new DecimalFormat("###,###").format(totalPrice));
    model.addAttribute("totalPrice", new DecimalFormat("###,###").format(totalPrice + 20000));
    model.addAttribute("products", cartItemServiceImpl.countProductsInCart());
    model.addAttribute("total", cartItemServiceImpl.getTotalPrice());
    List<OrderDto> orderDtos = orderService.getOrders();
    model.addAttribute("orders", orderDtos);
    return "cart";
  }

  @RequestMapping(value = { "/add" }, method = RequestMethod.POST)
  public String addToCart(@ModelAttribute("cartItem") CartItem cartItem) {
    cartItemServiceImpl.addCartItem(cartItem);
    return "redirect:/cart";
  }

  @GetMapping("/controller/delete/{id}")
  public String deleteCategory(@PathVariable("id") String id, RedirectAttributes ra) {
    try {
      userDetailsServiceImpl.delete(id);
      ra.addFlashAttribute("message", "The user ID " + id + " has been deleted.");
    } catch (UserNotFoundException e) {
      ra.addFlashAttribute("message", e.getMessage());
    }
    return "redirect:/users";
  }

}
