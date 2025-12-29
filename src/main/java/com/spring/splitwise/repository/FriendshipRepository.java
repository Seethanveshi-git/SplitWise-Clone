package com.spring.splitwise.repository;

import com.spring.splitwise.model.Friendship;
import com.spring.splitwise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    boolean existsByUserAndFriend(User user, User friend);
}