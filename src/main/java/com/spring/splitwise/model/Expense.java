package com.spring.splitwise.model;

import jakarta.persistence.*;
import com.spring.splitwise.model.User;
import com.spring.splitwise.model.Group;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by", nullable = false)
    private User paidBy;

    // nullable → group OR direct friend expense
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(mappedBy = "expense",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ExpenseSplit> splits = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
