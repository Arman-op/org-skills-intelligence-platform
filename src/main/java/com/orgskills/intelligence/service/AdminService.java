package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.auth.UserProfileResponse;
import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.exception.ResourceNotFoundException;
import com.orgskills.intelligence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserProfileResponse> getAllUsers(String department) {
        List<User> users;
        if (department != null && !department.isBlank()) {
            users = userRepository.findByDepartmentIgnoreCase(department.trim());
        } else {
            users = userRepository.findAll();
        }
        return users.stream().map(this::toProfile).toList();
    }

    public UserProfileResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + id));
        return toProfile(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found for id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserProfileResponse toProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .department(user.getDepartment())
                .jobTitle(user.getJobTitle())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
