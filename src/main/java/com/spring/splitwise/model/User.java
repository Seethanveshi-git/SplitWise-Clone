package com.spring.splitwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "paidTo")
    private List<Payment> paymentsReceived;


    @OneToMany(mappedBy = "user")
    private List<Member> memberships;


    // Expenses paid by this user
    @OneToMany(mappedBy = "paidBy")
    private List<Expense> expensesPaid;

    // Payments
    @OneToMany(mappedBy = "paidBy")
    private List<Payment> paymentsMade;



}
