package com.spring.splitwise.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="friendship")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="friend_id",nullable = false)
    private User friend;

    private String status;
    private LocalDateTime createdAt;

}
