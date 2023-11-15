package com.example.springcommerce.product;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.example.springcommerce.dto.OrderDto;
import com.example.springcommerce.service.CartItemServiceImpl;
import com.example.springcommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.springcommerce.model.CartItem;
import com.example.springcommerce.service.UserDetailsServiceImpl;

@Controller
public class ProductController {
    @Autowired
    private ProductService service;
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private CartItemServiceImpl cartItemServiceImpl;

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public String showProductList(
            @RequestParam(name = "keyword", defaultValue = "") Optional<String> keyword,
            @RequestParam(defaultValue = "0") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            Model model) {
        model.asMap().clear();
        model.addAttribute("isLogin", userDetailsServiceImpl.isLogin());
        model.addAttribute("pageTitle", "Product List");
        model.addAttribute("isAdmin", userDetailsServiceImpl.isAdmin());
        model.addAttribute("isProduct", true);
        model.addAttribute("lastname", userDetailsServiceImpl.getLastname());
        model.addAttribute("firstname", userDetailsServiceImpl.getFirstname());
        model.addAttribute("products", cartItemServiceImpl.countProductsInCart());
        model.addAttribute("total", cartItemServiceImpl.getTotalPrice());
        List<OrderDto> orderDtos = orderService.getOrders();
        model.addAttribute("orders", orderDtos);
        String sortField = sort[0];
        String sortDirection = sort[1];
        Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Order order = new Order(direction, sortField);
        // System.out.println(order);

        PaginatedProductResponse paginatedProductResponse;
        if (keyword.isPresent()) {
            paginatedProductResponse = service
                    .filterBooks(keyword.get(), PageRequest.of(currentPage, pageSize, Sort.by(order)));
        } else {
            paginatedProductResponse = service
                    .readProducts(PageRequest.of(currentPage, pageSize, Sort.by(order)));
        }
        model.addAttribute("paginatedProductResponse", paginatedProductResponse);
        model.addAttribute("currentNumberProduct", Math.min(paginatedProductResponse.getNumberOfItems(), (currentPage
                + 1) * pageSize));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("keyword", keyword.get());
        model.addAttribute("pageSize", pageSize + 1);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        int totalPages = paginatedProductResponse.getNumberOfPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "products";
    }

    @GetMapping("/products/{id}")
    public String showDetail(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        model.asMap().clear();
        model.addAttribute("isLogin", userDetailsServiceImpl.isLogin());
        model.addAttribute("isAdmin", userDetailsServiceImpl.isAdmin());
        model.addAttribute("pageTitle", "Product Detail");
        model.addAttribute("isLogin", userDetailsServiceImpl.isLogin());
        model.addAttribute("lastname", userDetailsServiceImpl.getLastname());
        model.addAttribute("firstname", userDetailsServiceImpl.getFirstname());
        model.addAttribute("cartItem", new CartItem());
        model.addAttribute("products", cartItemServiceImpl.countProductsInCart());
        model.addAttribute("total", cartItemServiceImpl.getTotalPrice());
        List<OrderDto> orderDtos = orderService.getOrders();
        model.addAttribute("orders", orderDtos);
        Product product;
        try {
            product = service.get(id);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", product.getName());

            return "product_detail";
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }

    }

    @GetMapping("/products/new")
    public String showNewForm(Model model) {
        model.addAttribute("isLogin", userDetailsServiceImpl.isLogin());
        model.addAttribute("isAdmin", userDetailsServiceImpl.isAdmin());
        model.addAttribute("lastname", userDetailsServiceImpl.getLastname());
        model.addAttribute("firstname", userDetailsServiceImpl.getFirstname());
        model.addAttribute("product", new Product());
        model.addAttribute("pageTitle", "Thêm sản phẩm mới ");
        return "product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes ra) {
        service.save(product);
        ra.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        model.addAttribute("isLogin", userDetailsServiceImpl.isLogin());
        model.addAttribute("isAdmin", userDetailsServiceImpl.isAdmin());

        try {
            Product product = service.get(id);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit product (ID: " + id + ")");

            return "product_form";
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "The product ID " + id + " has been deleted.");
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/products";
    }
}
