package com.example.studentabsencetracker.service;

import com.example.studentabsencetracker.exception.BadRequestException;
import com.example.studentabsencetracker.exception.ResourceNotFoundException;
import com.example.studentabsencetracker.exception.UnauthorizedException;
import com.example.studentabsencetracker.model.dto.request.PasswordUpdateRequest;
import com.example.studentabsencetracker.model.dto.request.RoleUpdateRequest;
import com.example.studentabsencetracker.model.dto.request.UserRequest;
import com.example.studentabsencetracker.model.dto.response.PagedUserResponse;
import com.example.studentabsencetracker.model.dto.response.UserResponse;
import com.example.studentabsencetracker.model.entity.User;
import com.example.studentabsencetracker.model.enums.RoleType;
import com.example.studentabsencetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Existing methods (unchanged)...

    public PagedUserResponse getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> content = userPage.getContent().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        return new PagedUserResponse(
                content,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    public PagedUserResponse searchUsers(String query, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> userPage = userRepository.searchUsers(query, pageable);

        List<UserResponse> content = userPage.getContent().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        return new PagedUserResponse(
                content,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }
    // Add this to your UserService if it's not present
    public UserResponse createUser(UserRequest userRequest) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new ResourceNotFoundException("Username is already taken!");
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ResourceNotFoundException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setRole(userRequest.getRole());
        user.setActive(true);

        userRepository.save(user);

        return new UserResponse(user);
    }
    public UserResponse updatePassword(Long id, PasswordUpdateRequest passwordUpdateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Verify current password
        if (!passwordEncoder.matches(passwordUpdateRequest.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Check if new password and confirm password match
        if (!passwordUpdateRequest.getNewPassword().equals(passwordUpdateRequest.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        userRepository.save(user);

        return new UserResponse(user);
    }

    public UserResponse updateRole(Long id, RoleUpdateRequest roleUpdateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Only admin can change roles
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new UnauthorizedException("Only administrators can change user roles");
        }

        user.setRole(roleUpdateRequest.getRole());
        userRepository.save(user);

        return new UserResponse(user);
    }
}