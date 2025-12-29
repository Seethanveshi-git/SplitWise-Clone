package com.spring.splitwise.controller;

import com.spring.splitwise.model.Friend;
import com.spring.splitwise.model.User;
import com.spring.splitwise.service.FriendService;
import com.spring.splitwise.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    public FriendController(FriendService friendService,
                            UserService userService) {
        this.friendService = friendService;
        this.userService = userService;
    }


    @GetMapping("/{friendId}")
    public String viewFriend(@PathVariable Long friendId,
                             Model model) {

        User user = userService.getUserWithMembers(5L);

        User selectedFriend = userService.getById(friendId);
        Friend friendship =
                friendService.findByUserAndFriend(user, selectedFriend);

        model.addAttribute("user", user);
        model.addAttribute("friends", friendService.getFriends(user));
        model.addAttribute("selectedFriend", selectedFriend);
        model.addAttribute("friendship", friendship);
        model.addAttribute("view", "friend");

        return "dashboard";
    }


    @PostMapping("/invite")
    public String inviteFriend(@RequestParam String email,
                               RedirectAttributes redirectAttributes) {

        User user = userService.getUserWithMembers(5L);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                friendService.inviteFriend(user, email)
        );

        return "redirect:/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               Model model) {

        Friend friend = friendService.getPendingRequestForEdit(id);
        model.addAttribute("friend", friend);
        model.addAttribute("friendship", friend);

        return "edit-friend-info";
    }


    @PostMapping("/edit")
    public String editFriendRequest(@RequestParam Long friendId,
                                    @RequestParam String name,
                                    @RequestParam String email,
                                    RedirectAttributes redirectAttributes) {

        friendService.editFriend(friendId, name, email);
        redirectAttributes.addFlashAttribute("successMessage", "Friend updated");

        return "redirect:/dashboard";
    }


    @PostMapping("/remove/{id}")
    public String removeFriend(@PathVariable Long id) {

        friendService.removeFriendByFriendId(id);
        return "redirect:/dashboard";
    }


    @PostMapping("/resend/{id}")
    public String resendInvite(@PathVariable Long id) {

        friendService.resendInvite(id);
        return "redirect:/dashboard";
    }



}
