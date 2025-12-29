package com.spring.splitwise.controller;

import com.spring.splitwise.model.User;
import com.spring.splitwise.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AuthController {

    private UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Show register page
    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    // Handle register (ONLY DB INSERT)
    @PostMapping("/register")
    public String register(User user, Model model) {

        Optional<User> existingUserOpt = userRepository.findByEmail(user.getEmail());

        // Case 1: Email exists
        if(existingUserOpt.isPresent()){
            User existingUser = existingUserOpt.get();

            // CASE 1A: Already active user
            if (existingUser.isActive()) {
                model.addAttribute("error", "You already have an account. Please login.");
                return "register";
            }

            // CASE 1B: Invited user (inactive)
            existingUser.setPassword(user.getPassword());
            existingUser.setUsername(user.getUsername());
            existingUser.setActive(true);
            existingUser.setUpdatedAt(LocalDateTime.now());

            userRepository.save(existingUser);
            return "redirect:/login";
        }

        // CASE 2: Brand new user
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        return "redirect:/login";
    }

    // Show login page
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    // Handle login (ONLY DB READ)
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() ||
                !userOpt.get().getPassword().equals(password)) {

            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        // ✅ Store user in session
        session.setAttribute("loggedInUser", userOpt.get());

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }


}


