package com.orgskills.intelligence.service;

import com.orgskills.intelligence.dto.notification.NotificationResponse;
import com.orgskills.intelligence.entity.Notification;
import com.orgskills.intelligence.entity.Skill;
import com.orgskills.intelligence.entity.User;
import com.orgskills.intelligence.entity.enums.NotificationType;
import com.orgskills.intelligence.exception.ResourceNotFoundException;
import com.orgskills.intelligence.repository.NotificationRepository;
import com.orgskills.intelligence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Notification createGapAlert(User user, Skill skill, double gapScore) {
        String title = "Critical skill gap detected: " + skill.getName();
        if (notificationRepository.existsByUserIdAndTypeAndTitleAndIsReadFalse(
                user.getId(), NotificationType.GAP_ALERT, title)) {
            return null;
        }
        String message = "Your current proficiency gap for " + skill.getName() + " is " + String.format("%.2f", gapScore)
                + ". Immediate development action is recommended.";
        return createNotification(user, title, message, NotificationType.GAP_ALERT);
    }

    @Transactional
    public Notification createMentorshipInvite(User user, String message) {
        return createNotification(user, "Mentorship invitation", message, NotificationType.MENTORSHIP_INVITE);
    }

    @Transactional
    public Notification createNotification(User user, String title, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);

        Notification saved = notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + user.getId(), toResponse(saved));
        return saved;
    }

    public List<NotificationResponse> getByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found for id: " + id));
        notification.setIsRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
