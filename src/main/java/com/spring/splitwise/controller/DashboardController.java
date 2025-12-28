package com.spring.splitwise.controller;

import com.spring.splitwise.model.User;
import com.spring.splitwise.service.FriendService;
import com.spring.splitwise.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private FriendService friendService;
    private UserService userService;

    public DashboardController(UserService userService,
                               FriendService friendService) {
        this.userService = userService;
        this.friendService = friendService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) Long friendId,
            Model model) {

        // TEMP way to get logged-in user (replace with Security later)
        User currentUser = userService.getByEmail("john@gmail.com");

        model.addAttribute("user", currentUser);
        model.addAttribute("friends", friendService.getFriends(currentUser));
        if(friendId != null){
            User selectedFriend = userService.getById(friendId);
            model.addAttribute("selectedFriend",selectedFriend);
        }

        // calculate later
        return "dashboard";
    }
}