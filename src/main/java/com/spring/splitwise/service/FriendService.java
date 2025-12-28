package com.spring.splitwise.service;

import com.spring.splitwise.model.Friendship;
import com.spring.splitwise.model.User;
import com.spring.splitwise.repository.FriendshipRepository;
import com.spring.splitwise.repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FriendService {

    private FriendshipRepository friendshipRepository;
    private UserRepository userRepository;

    public FriendService(FriendshipRepository friendshipRepository,
                         UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    public List<Friendship> getFriends(User currentUser) {
        return friendshipRepository.findByUserId(currentUser.getId());
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
            // INVITED USER (THIS IS WHERE YOUR CODE GOES)

            friend = new User();
            friend.setEmail(email);
            friend.setUsername(email.split("@")[0]);

            // IMPORTANT: random unusable password
            friend.setPassword(UUID.randomUUID().toString());

            // invited user (cannot login)
            friend.setActive(false);

            friend = userRepository.save(friend);

            status = "PENDING";
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


    public void removeFriend(Long friendshipId) {
        friendshipRepository.deleteById(friendshipId);
    }
}
