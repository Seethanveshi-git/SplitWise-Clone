package com.spring.splitwise.service;

import com.spring.splitwise.model.Friendship;
import com.spring.splitwise.model.User;
import com.spring.splitwise.repository.FriendshipRepository;
import com.spring.splitwise.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FriendshipService {

    private FriendshipRepository friendshipRepository;
    private UserRepository userRepository;


    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public List<Friendship> getFriends(User currentUser) {
        return friendshipRepository.findByUserIdOrderByFriend_UsernameAsc(currentUser.getId());
    }

    public String inviteFriend(User currentUser, String email) {

        Optional<User> friendOptional = userRepository.findByEmail(email);

        User friend;
        String status;

        if (friendOptional.isPresent()) {
            // Existing registered user
            friend = friendOptional.get();
            status = "ACTIVE";

        } else {
            friend = new User();
            friend.setEmail(email);
            friend.setUsername(email.split("@")[0]);
            friend.setPassword("password123");
            friend.setActive(false);
            friend = userRepository.save(friend);
            status = "PENDING";
        }
        if (friendshipRepository.existsByUserAndFriend(currentUser, friend)) {
            return "Friend request already sent!";
        }

        Friendship friendship = new Friendship();
        friendship.setUser(currentUser);
        friendship.setFriend(friend);
        friendship.setStatus(status);

        friendshipRepository.save(friendship);

        return status.equals("ACTIVE")
                ? "Friend Added Successfully"
                : "Invite sent. Pending acceptance.";
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

    public void editFriend(Long friendId,String name, String email) {

        Friendship friendship = friendshipRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!friendship.getStatus().equals("PENDING")) {
            throw new RuntimeException("Edit not allowed");
        }
//        if (emailAlreadyExistsForAnotherUser(email)) {
//            throw new RuntimeException("Email already in use");
//        }

        User friend = friendship.getFriend();

        friend.setEmail(email);
        friend.setUsername(name);

        userRepository.save(friend);
    }

    public void resendInvite(Long friendshipId) {

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!friendship.getStatus().equals("PENDING")) {
            throw new RuntimeException("Edit not allowed");
        }

        friendship.setCreatedAt(LocalDateTime.now());
        friendshipRepository.save(friendship);
    }

    public void removeFriendByFriendshipId(Long id) {
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        User user = friendship.getUser();
        User friend = friendship.getFriend();

        friendshipRepository.delete(friendship);

        friendshipRepository.findByUserAndFriend(friend, user)
                .ifPresent(friendshipRepository::delete);
    }
}
