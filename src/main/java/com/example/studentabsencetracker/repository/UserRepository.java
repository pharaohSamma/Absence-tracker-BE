package com.example.studentabsencetracker.repository;

import com.example.studentabsencetracker.model.entity.User;
import com.example.studentabsencetracker.model.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRole(RoleType role);
//    Page<User> findByRole(RoleType role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);
}