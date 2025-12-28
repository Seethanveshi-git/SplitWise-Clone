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

    @GetMapping
    public String friendList(Model model){
        User currentUser = new User();

        model.addAttribute("friends",friendshipService.getFriends(currentUser)) ;
        return "dashboard";
    }

//    @GetMapping("/{friendId}")
//    public String viewFriend(@PathVariable Long friendId, Model model) {
//
//        // TEMP user (until Spring Security)
//        User currentUser = userRepository.findById(1L)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Selected friend
//        User selectedFriend = userRepository.findById(friendId)
//                .orElseThrow(() -> new RuntimeException("Friend not found"));
//
//        // Friendship relation
//        Friendship friendship = friendshipRepository
//                .findByUserAndFriend(currentUser, selectedFriend)
//                .orElse(null);
//
//        model.addAttribute("user", currentUser);
//        model.addAttribute("friends", friendshipService.getFriends(currentUser));
//        model.addAttribute("selectedFriend", selectedFriend);
//        model.addAttribute("friendship", friendship);
//
//        return "dashboard";
//    }

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
    public String removeFriend(@PathVariable Long id) {
        friendshipService.removeFriend(id);
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
