package com.spring.splitwise.controller;

import com.spring.splitwise.model.Group;
import com.spring.splitwise.model.User;
import com.spring.splitwise.repository.UserRepository;
import com.spring.splitwise.repository.*;
import com.spring.splitwise.service.GroupService;
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
    private UserRepository userRepository;

    public GroupController(GroupService groupService, UserRepository userRepository){
        this.groupService = groupService;
        this.userRepository = userRepository;
    }

    private User getHardcodedUser() {
        return userRepository.findById(1L).orElseThrow();
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("user", getHardcodedUser());
        model.addAttribute("view", "dashboard");
        return "dashboard";
    }
//
//    @GetMapping("/groups/view/{id}")
//    public String viewGroup(@PathVariable Long id, Model model) {
//        model.addAttribute("user", getHardcodedUser());
//        model.addAttribute("selectedGroup", groupService.findById(id).orElseThrow());
//        model.addAttribute("view", "details");
//        return "dashboard";
//    }
//
//    @PostMapping("/groups/save")
//    public String saveGroup(@RequestParam String groupName) {
//        Group group = new Group();
//        group.setGroupName(groupName);
//        group.setCreatedBy(getHardcodedUser());
//        groupService.save(group);
//        return "redirect:/dashboard";
//    }




    @GetMapping
    public String showlistgroups(Model model){
        List<Group> listOfGroups =  this.groupService.getGroupsForUser(1L);
        System.out.println(listOfGroups.size());
        return "dashboard";
    }

    @GetMapping("/newgroup")
    public String showCreateGroupForm(Model model) {
        Group group = new Group();
        model.addAttribute("group", group);
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
    public String viewGroup(@PathVariable Long id, Model model) {
        User user = getHardcodedUser();
        Group group = groupService.findById(id);

        model.addAttribute("user", user);
        model.addAttribute("selectedGroup", group);
        model.addAttribute("view", "details");

        return "dashboard"; // same Thymeleaf template
    }

}