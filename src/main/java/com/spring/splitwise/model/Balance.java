package com.spring.splitwise.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "balances")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceId;

    // Group context
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    // Who is owed money
    @ManyToOne
    @JoinColumn(name = "lender")
    private User lender;

    // Who owes money
    @ManyToOne
    @JoinColumn(name = "borrower")
    private User borrower;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

