package com.spring.splitwise.service;

import com.spring.splitwise.model.Friend;
import com.spring.splitwise.model.User;
import com.spring.splitwise.repository.FriendRepository;
import com.spring.splitwise.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FriendService {

    private FriendRepository friendRepository;
    private UserRepository userRepository;


    public FriendService(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    public List<Friend> getFriends(User currentUser) {
        return friendRepository.findByUserIdOrderByFriend_UsernameAsc(currentUser.getId());
    }

    public String inviteFriend(User currentUser, String email) {

        Optional<User> friendOptional = userRepository.findByEmail(email);
        User friend;

        if (friendOptional.isPresent()) {
            // Existing registered user
            friend = friendOptional.get();
            // 🔹 CASE 1: User exists AND active
            if (friend.isActive()) {
                createMutualFriend(currentUser, friend, "ACTIVE");
                return "Friend added successfully";
            }
            // 🔹 CASE 2: User exists BUT inactive
            createSinglePendingFriend(currentUser, friend);
            return "Invite sent. Waiting for user to join.";
        }
        // 🔹 CASE 3: User does not exist
        friend = new User();
        friend.setEmail(email);
        friend.setUsername(email.split("@")[0]);
        friend.setPassword(UUID.randomUUID().toString());
        friend.setActive(false);

        friend = userRepository.save(friend);

        createSinglePendingFriend(currentUser, friend);
        return "Invite sent. Waiting for user to join.";
    }

    public Friend getPendingRequestForEdit(Long id) {

        Friend friend = friendRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!friend.getStatus().equals("PENDING")) {
            throw new RuntimeException("Edit not allowed");
        }

        return friend;
    }

    public void editFriend(Long friendId,String name, String email) {

        Friend Friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!Friend.getStatus().equals("PENDING")) {
            throw new RuntimeException("Edit not allowed");
        }
//        if (emailAlreadyExistsForAnotherUser(email)) {
//            throw new RuntimeException("Email already in use");
//        }

        User friend = Friend.getFriend();

        friend.setEmail(email);
        friend.setUsername(name);

        userRepository.save(friend);
    }

    public void resendInvite(Long FriendId) {

        Friend friend = friendRepository.findById(FriendId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!friend.getStatus().equals("PENDING")) {
            throw new RuntimeException("Edit not allowed");
        }

        friend.setCreatedAt(LocalDateTime.now());
        friendRepository.save(friend);
    }

    public void removeFriendByFriendId(Long id) {
        Friend Friend = friendRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        User user = Friend.getUser();
        User friend = Friend.getFriend();

        friendRepository.delete(Friend);

        friendRepository.findByUserAndFriend(friend, user)
                .ifPresent(friendRepository::delete);
    }

    public Friend findByUserAndFriend(User currentUser, User selectedFriend){
        return friendRepository.findByUserAndFriend(currentUser , selectedFriend).orElse(null);
    }

    private void createMutualFriend(User u1, User u2, String status) {
        if(u1.getId().equals(u2.getId()))
            return ;

        // prevent duplicates
        if (friendRepository.existsByUserAndFriend(u1, u2)) {
            return;
        }

        Friend f1 = new Friend();
        f1.setUser(u1);
        f1.setFriend(u2);
        f1.setStatus(status);

        Friend f2 = new Friend();
        f2.setUser(u2);
        f2.setFriend(u1);
        f2.setStatus(status);

        friendRepository.save(f1);
        friendRepository.save(f2);
    }

    private void createSinglePendingFriend(User inviter, User invitee) {

        if (inviter.getId().equals(invitee.getId())) {
            return;
        }

        if (friendRepository.existsByUserAndFriend(inviter, invitee)) {
            return;
        }

        Friend Friend = new Friend();
        Friend.setUser(inviter);
        Friend.setFriend(invitee);
        Friend.setStatus("PENDING");

        friendRepository.save(Friend);
    }
}
