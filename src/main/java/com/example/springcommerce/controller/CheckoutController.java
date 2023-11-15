package com.example.springcommerce.controller;

import java.util.List;

import com.example.springcommerce.dto.OrderDto;
import com.example.springcommerce.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.springcommerce.model.CartItem;
import com.example.springcommerce.model.Order;
import com.example.springcommerce.model.OrderDetail;
import com.example.springcommerce.product.ProductService;
import com.example.springcommerce.service.CartItemServiceImpl;
import com.example.springcommerce.service.OrderDetailService;
import com.example.springcommerce.service.OrderService;
import com.example.springcommerce.service.UserDetailsServiceImpl;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    CartItemServiceImpl cartItemServiceImpl;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    ProductService productService;

    @RequestMapping(value = { "" }, method = RequestMethod.POST)
    @Transactional
    public String checkout(Model model) {
        List<CartItem> carts = cartItemServiceImpl.getCurrentCartsByCurrentUser();

        if (carts.size() == 0) {
            return "redirect:/cart";
        }

        Order order = new Order();
        order.setFullName(userDetailsServiceImpl.getCurrentUserId());
        order.setPhone(userDetailsServiceImpl.getCurrentUser().getPhone());
        order.setAddress(userDetailsServiceImpl.getCurrentUser().getAddress());
        Order save = orderService.save(order);

        List<OrderDetail> list = carts.stream().map(arg0 -> new OrderDetail(
                save,
                productService.getById(arg0.getProductId()),
                arg0.getQuantity(), arg0.getProductPrice(),
                arg0.getQuantity())).toList();

        list.stream().forEach(arg0 -> orderDetailService.save(arg0));

        cartItemServiceImpl.clearCurrentCartByCurrentUser();

        return "redirect:/checkout";
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String pageCheckOut(Model model) {
        model.asMap().clear();
        model.addAttribute("isLogin", userDetailsServiceImpl.isLogin());
        model.addAttribute("pageTitle", "Checkout");
        model.addAttribute("isAdmin", userDetailsServiceImpl.isAdmin());
        model.addAttribute("lastname", userDetailsServiceImpl.getLastname());
        model.addAttribute("firstname", userDetailsServiceImpl.getFirstname());
        model.addAttribute("products", cartItemServiceImpl.countProductsInCart());
        List<OrderDto> orderDtos = orderService.getOrders();
        model.addAttribute("orders", orderDtos);
        return "checkout-success";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") String id, RedirectAttributes ra) {
        try {
            userDetailsServiceImpl.delete(id);
            ra.addFlashAttribute("message", "The user ID " + id + " has been deleted.");
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }

}