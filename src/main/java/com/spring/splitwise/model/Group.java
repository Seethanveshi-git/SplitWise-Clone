package com.spring.splitwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.management.ConstructorParameters;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

@Entity
@Table(name= "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(nullable = false)
    private String groupName;

    private String groupDescription;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "group")
    private List<Member> members;




//
//    // Expenses in the group
//    @OneToMany(mappedBy = "group")
//    private List<Expense> expenses;
//
//    // Balances in the group
//    @OneToMany(mappedBy = "group")
//    private List<Balance> balances;
}
