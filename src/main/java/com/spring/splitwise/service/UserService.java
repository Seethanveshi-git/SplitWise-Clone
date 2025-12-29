package com.spring.splitwise.service;

import com.spring.splitwise.model.User;
import com.spring.splitwise.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserWithMembers(Long id) {
        return userRepository.findWithMembersById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(null);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
