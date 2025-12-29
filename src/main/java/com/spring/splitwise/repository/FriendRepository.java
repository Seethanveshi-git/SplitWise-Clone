package com.spring.splitwise.repository;

import com.spring.splitwise.model.Friend;
import com.spring.splitwise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>{

    List<Friend> findByUserIdOrderByFriend_UsernameAsc(Long userId);

    Optional<Friend> findByUserAndFriend(User user, User friend);

    void deleteByUserAndFriend(User user, User friend);

    boolean existsByUserAndFriend(User user, User friend);
}
