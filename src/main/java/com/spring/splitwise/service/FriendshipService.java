package com.spring.splitwise.service;

import com.spring.splitwise.model.Friendship;
import com.spring.splitwise.model.User;
import com.spring.splitwise.repository.FriendshipRepository;
import com.spring.splitwise.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FriendshipService {

    private FriendshipRepository friendshipRepository;
    private UserRepository userRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public void removeFriend(Long friendUserId){

        User user = userRepository.findById(1L).get();
        User friend = userRepository.findById(friendUserId)
                .orElseThrow(()->new RuntimeException("Friend not found"));

        Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend)
                .orElseThrow(()->new RuntimeException("Friendship not found"));

        friendshipRepository.deleteByUserAndFriend(user, friend);
        friendshipRepository.deleteByUserAndFriend(friend, user);
    }


    public Friendship getPendingRequestForEdit(Long id) {

        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!friendship.getStatus().equals("PENDING")) {
            throw new RuntimeException("Edit not allowed");
        }

        return friendship;
    }

    public void editFriend(Long friendId, String email) {

        Friendship friendship = friendshipRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!friendship.getStatus().equals("PENDING")) {
            throw new RuntimeException("Edit not allowed");
        }

        User user = friendship.getUser();

        friendshipRepository.delete(friendship);

        User friend = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getId().equals(friend.getId())) {
            throw new RuntimeException("Cannot send request to yourself");
        }

        Friendship newRequest = new Friendship();
        newRequest.setUser(user);
        newRequest.setFriend(friend);
        newRequest.setStatus("PENDING");
        newRequest.setCreatedAt(LocalDateTime.now());

        friendshipRepository.save(newRequest);
    }

    public void resendInvite(Long friendshipId) {

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!friendship.getStatus().equals("PENDING")) {
            throw new RuntimeException("Edit not allowed");
        }

        // Optional: update timestamp to indicate resend
        friendship.setCreatedAt(LocalDateTime.now());
        friendshipRepository.save(friendship);
    }
}
