package com.spring.splitwise.controller;

import com.spring.splitwise.model.Friendship;
import com.spring.splitwise.model.User;
import com.spring.splitwise.repository.FriendshipRepository;
import com.spring.splitwise.repository.UserRepository;
import com.spring.splitwise.service.FriendshipService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/friends")
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final FriendshipRepository friendshipRepository;
    private UserRepository userRepository;

    public FriendshipController(FriendshipService friendshipService, FriendshipRepository friendshipRepository) {
        this.friendshipService = friendshipService;
        this.friendshipRepository = friendshipRepository;
    }

    @PostMapping("/remove/{friendId}")
    public String removeFriend(@PathVariable Long friendId){
        friendshipService.removeFriend(friendId);
        return "redirect:/friends";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        Friendship friendship = friendshipService.getPendingRequestForEdit(id);

        model.addAttribute("friendship", friendship);
        return "edit-friend";
    }

    @PostMapping("/edit")
    public String editFriendRequest(@RequestParam Long friendId,
                                    @RequestParam String email) {

        friendshipService.editFriend(friendId, email);

        return "redirect:/friends";
    }

    @PostMapping("/resend/{id}")
    public String resendInvite(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {

        friendshipService.resendInvite(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Invite sent successfully"
        );

        return "redirect:/friends";
    }

}
