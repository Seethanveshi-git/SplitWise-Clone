package com.spring.splitwise.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reminderId;

    // Who sent reminder
    @ManyToOne
    @JoinColumn(name = "sent_by")
    private User sentBy;

    // Who received reminder
    @ManyToOne
    @JoinColumn(name = "sent_to")
    private User sentTo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private LocalDateTime sentAt;
}
