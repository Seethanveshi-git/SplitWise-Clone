package com.spring.splitwise.controller;

import com.spring.splitwise.model.User;
import com.spring.splitwise.service.FriendService;
import com.spring.splitwise.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final FriendService friendService;
    private final UserService userService;

    public DashboardController(FriendService friendService,
                               UserService userService) {
        this.friendService = friendService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        User user = userService.getUserWithMembers(5L);

        model.addAttribute("user", user);
        model.addAttribute("friends", friendService.getFriends(user));
        model.addAttribute("view", "dashboard");

        return "dashboard";
    }

}
