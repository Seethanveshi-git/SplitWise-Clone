package com.spring.splitwise.controller;

import com.spring.splitwise.model.User;
import com.spring.splitwise.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public String userProfile(@PathVariable Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "user-profile";
    }
}
