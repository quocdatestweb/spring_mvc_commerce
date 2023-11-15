package com.example.springcommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springcommerce.service.UserDetailsServiceImpl;

@Controller
public class SignInController {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @GetMapping("/login")
    public String signIn(Model model) {
        model.addAttribute("isSignIn", true);
        model.addAttribute("pageTitle", "Sign In");
        model.addAttribute("isLogin", userDetailsServiceImpl.isLogin());

        return "login";
    }
}