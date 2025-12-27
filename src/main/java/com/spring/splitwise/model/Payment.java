package com.spring.splitwise.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    // Who paid
    @ManyToOne
    @JoinColumn(name = "paid_by")
    private User paidBy;

    // Who received payment
    @ManyToOne
    @JoinColumn(name = "paid_to")
    private User paidTo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private LocalDateTime paidAt;
}
