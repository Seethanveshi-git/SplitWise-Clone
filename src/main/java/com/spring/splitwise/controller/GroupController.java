package com.spring.splitwise.controller;

import com.spring.splitwise.model.Group;
import com.spring.splitwise.model.User;
import com.spring.splitwise.repository.UserRepository;
import com.spring.splitwise.repository.*;
import com.spring.splitwise.service.FriendService;
import com.spring.splitwise.service.GroupService;
import com.spring.splitwise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;
    private FriendService friendService;

    public GroupController(GroupService groupService, UserService userService , FriendService friendService){
        this.groupService = groupService;
        this.userService = userService;
        this.friendService = friendService;
    }

    private User getHardcodedUser() {
        return userService.findById(1L);
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        User user = getHardcodedUser();
        model.addAttribute("user", user);
        model.addAttribute("friends", friendService.getFriends(user));
        model.addAttribute("view", "dashboard");
        return "dashboard";
    }

    @GetMapping
    public String showlistgroups(Model model){
        User user = getHardcodedUser();

        model.addAttribute("user", user);
        model.addAttribute("friends", friendService.getFriends(user));
        model.addAttribute("view", "dashboard");

        return "dashboard";
    }

    @GetMapping("/newgroup")
    public String showCreateGroupForm(Model model) {
        Group group = new Group();
        model.addAttribute("group", group);
        model.addAttribute("isEdit", false);
        return "create-group";
    }

    @PostMapping("/save")
    public String saveGroup(@ModelAttribute Group group,
                            @RequestParam("memberEmails") List<String> memberEmails,
                            @RequestParam("memberNames") List<String> memberNames) {
        Long creatorId = 1L;
        groupService.createNewGroup(group, creatorId, memberEmails, memberNames);
        return "redirect:/groups/";
    }

    @GetMapping("/view/{id}")
    public String viewGroup(@PathVariable Long id,
                            Model model) {

        User user = userService.getUserWithMembers(1L);
        Group group = groupService.findById(id);

        model.addAttribute("user", user);
        model.addAttribute("friends", friendService.getFriends(user));
        model.addAttribute("selectedGroup", group);
        model.addAttribute("view", "details");

        return "dashboard";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Group group = groupService.findById(id);
        model.addAttribute("group", group);
        model.addAttribute("isEdit", true);
        return "create-group";
    }

    @PostMapping("/update")
    public String updateGroup(@ModelAttribute("group") Group group,
                              @RequestParam(value = "memberNames", required = false) List<String> names,
                              @RequestParam(value = "memberEmails", required = false) List<String> emails) {

        groupService.updateExistingGroup(group.getGroupId(), group.getGroupName(), names, emails);

        return "redirect:/groups/view/" + group.getGroupId();
    }

    @PostMapping("/delete/{id}")
    public String deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return "redirect:/groups/dashboard";
    }
}