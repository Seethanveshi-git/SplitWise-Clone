package com.spring.splitwise.controller;

import com.spring.splitwise.model.User;
import com.spring.splitwise.service.FriendService;
import com.spring.splitwise.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/friends")
public class FriendController {

    private FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping
    public String friendList(Model model){
        // Temp : Logged inn user
        User currentUser = new User();

        model.addAttribute("friends",friendService.getFriends(currentUser)) ;
        return "dashboard";
    }

    @PostMapping("/invite")
    public String inviteFriend(@RequestParam String email,
                               RedirectAttributes redirectAttributes) {

        User currentUser = new User();
        currentUser.setId(1L);

        String message = friendService.inviteFriend(currentUser, email);
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/dashboard";
    }

    @PostMapping("/remove/{id}")
    public String removeFriend(@PathVariable Long id) {
        friendService.removeFriend(id);
        return "redirect:/friends";
    }
}
