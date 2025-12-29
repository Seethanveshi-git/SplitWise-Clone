package com.spring.splitwise.repository;

import com.spring.splitwise.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("""
        SELECT DISTINCT g FROM Group g
        JOIN g.members m
        WHERE g.createdBy.id = :userId
           OR m.user.id = :userId
    """)
    List<Group> findGroupsByUserId(Long userId);
}