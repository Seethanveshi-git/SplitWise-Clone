package com.spring.splitwise.repository;

import com.spring.splitwise.model.Friendship;
import com.spring.splitwise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long>{
    List<Friendship> findByUserId(Long userId);

    Optional<Friendship> findByUserAndFriend(User user, User friend);
}
