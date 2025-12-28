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

    public FriendshipController(FriendshipService friendshipService, FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipService = friendshipService;
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        return userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public String friendList(Model model){
        User currentUser = userRepository.findById(1L).get();
        model.addAttribute("friends", friendshipService.getFriends(currentUser));
        return "dashboard";
    }

    @GetMapping("/{friendId}")
    public String viewFriend(@PathVariable Long friendId, Model model) {

        User currentUser = getCurrentUser();

        User selectedFriend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        Friendship friendship = friendshipRepository
                .findByUserAndFriend(currentUser, selectedFriend)
                .orElse(null);

        model.addAttribute("user", currentUser);
        model.addAttribute("friends", friendshipService.getFriends(currentUser));
        model.addAttribute("selectedFriend", selectedFriend);
        model.addAttribute("friendship", friendship);
        model.addAttribute("view", "friend");

        return "dashboard";
    }

    @PostMapping("/invite")
    public String inviteFriend(@RequestParam String email,
                               RedirectAttributes redirectAttributes) {

        User currentUser = new User();
        currentUser.setId(1L);

        String message = friendshipService.inviteFriend(currentUser, email);
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/dashboard";
    }

    @PostMapping("/remove/{id}")
    public String removeFriend(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {

        friendshipService.removeFriendByFriendshipId(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Friend removed successfully"
        );

        return "redirect:/dashboard";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        Friendship friendship = friendshipService.getPendingRequestForEdit(id);

        model.addAttribute("friendship", friendship);
        model.addAttribute("friend", friendship.getFriend());
        return "edit-friend-info";
    }

    @PostMapping("/edit")
    public String editFriendRequest(@RequestParam Long friendId,
                                    @RequestParam String name,
                                    @RequestParam String email) {

        friendshipService.editFriend(friendId, name, email);

        return "redirect:/dashboard";
    }


    @PostMapping("/resend/{id}")
    public String resendInvite(@PathVariable Long id, Model model) {

        Friendship friendship = friendshipService.getPendingRequestForEdit(id);

        friendshipService.resendInvite(id);

        User currentUser = friendship.getUser();
        User selectedFriend = friendship.getFriend();

        model.addAttribute("user", currentUser);
        model.addAttribute("friends", friendshipService.getFriends(currentUser));
        model.addAttribute("selectedFriend", selectedFriend);
        model.addAttribute("friendship", friendship);
        model.addAttribute("view", "friend");

        model.addAttribute("successMessage", "Invite sent successfully");

        return "dashboard";
    }
}
