package com.spring.splitwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User in the group
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Group the user belongs to
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private LocalDateTime joinedAt;
    private LocalDateTime deletedAt;
}

