package com.example.studentabsencetracker.model.entity;

import com.example.studentabsencetracker.model.enums.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Teacher relationship - classes taught
    @OneToMany(mappedBy = "teacher")
    private Set<CourseClass> taughtCourseClasses = new HashSet<>();

    // Student relationship - enrolled classes
    @ManyToMany(mappedBy = "students")
    private Set<CourseClass> enrolledCourseClasses = new HashSet<>();

    // Parent-Student relationship (if user is a parent)
    @ManyToMany
    @JoinTable(
            name = "parent_student",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<User> children = new HashSet<>();

    // Parent-Student relationship (if user is a student)
    @ManyToMany(mappedBy = "children")
    private Set<User> parents = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods for managing parent-student relationships
    public void addChild(User child) {
        this.children.add(child);
        child.getParents().add(this);
    }

    public void removeChild(User child) {
        this.children.remove(child);
        child.getParents().remove(this);
    }
}