package com.example.studentabsencetracker.service;

import com.example.studentabsencetracker.model.entity.Absence;
import com.example.studentabsencetracker.model.entity.CourseClass;
import com.example.studentabsencetracker.model.entity.Notification;
import com.example.studentabsencetracker.model.entity.User;
import com.example.studentabsencetracker.model.enums.NotificationType;
import com.example.studentabsencetracker.model.enums.RoleType;
import com.example.studentabsencetracker.repository.NotificationRepository;
import com.example.studentabsencetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public void createAbsenceRecordedNotification(Absence absence) {
        // Create a notification for the student
        createNotification(
                absence.getStudent(),
                NotificationType.ABSENCE_RECORDED,
                "Absence Recorded",
                "An absence has been recorded for you in " + absence.getCourseClass().getName() + " on " + absence.getDate(),
                absence.getId(),
                "Absence"
        );
    }

    public void createAbsenceExcusedNotification(Absence absence) {
        // Create a notification for the student
        createNotification(
                absence.getStudent(),
                NotificationType.GENERAL,
                "Absence Excused",
                "Your absence in " + absence.getCourseClass().getName() + " on " + absence.getDate() + " has been marked as excused.",
                absence.getId(),
                "Absence"
        );
    }

    public void createParentAbsenceNotification(Absence absence) {
        // Create notifications for all parents of the student
        absence.getStudent().getParents().forEach(parent -> {
            createNotification(
                    parent,
                    NotificationType.ABSENCE_RECORDED,
                    "Child Absence Recorded",
                    "An absence has been recorded for " + absence.getStudent().getFirstName() + " " +
                            absence.getStudent().getLastName() + " in " + absence.getCourseClass().getName() +
                            " on " + absence.getDate(),
                    absence.getId(),
                    "Absence"
            );
        });
    }

    public void createClassAbsenceThresholdNotification(User student, CourseClass courseClass, long absenceCount) {
        // Notify student
        createNotification(
                student,
                NotificationType.ABSENCE_THRESHOLD_REACHED,
                "Absence Threshold Reached for Class",
                "You have reached " + absenceCount + " unexcused absences in " + courseClass.getName() +
                        ". This may affect your standing in the class.",
                courseClass.getId(),
                "CourseClass"
        );

        // Notify parents
        student.getParents().forEach(parent -> {
            createNotification(
                    parent,
                    NotificationType.ABSENCE_THRESHOLD_REACHED,
                    "Child Absence Threshold Reached for Class",
                    student.getFirstName() + " " + student.getLastName() + " has reached " +
                            absenceCount + " unexcused absences in " + courseClass.getName() +
                            ". This may affect their standing in the class.",
                    courseClass.getId(),
                    "CourseClass"
            );
        });

        // Notify teacher
        createNotification(
                courseClass.getTeacher(),
                NotificationType.ABSENCE_THRESHOLD_REACHED,
                "Student Absence Threshold Reached",
                student.getFirstName() + " " + student.getLastName() + " has reached " +
                        absenceCount + " unexcused absences in your class " + courseClass.getName() + ".",
                student.getId(),
                "User"
        );
    }

    public void createOverallAbsenceThresholdNotification(User student, long absenceCount) {
        // Notify student
        createNotification(
                student,
                NotificationType.ABSENCE_THRESHOLD_REACHED,
                "Overall Absence Threshold Reached",
                "You have reached " + absenceCount + " total unexcused absences across all classes. " +
                        "This may affect your academic standing.",
                student.getId(),
                "User"
        );

        // Notify parents
        student.getParents().forEach(parent -> {
            createNotification(
                    parent,
                    NotificationType.ABSENCE_THRESHOLD_REACHED,
                    "Child Overall Absence Threshold Reached",
                    student.getFirstName() + " " + student.getLastName() + " has reached " +
                            absenceCount + " total unexcused absences across all classes. " +
                            "This may affect their academic standing.",
                    student.getId(),
                    "User"
            );
        });

        // Notify admin users
        List<User> adminUsers = userRepository.findByRole(RoleType.ADMIN);
        adminUsers.forEach(admin -> {
            createNotification(
                    admin,
                    NotificationType.ABSENCE_THRESHOLD_REACHED,
                    "Student Overall Absence Threshold Reached",
                    student.getFirstName() + " " + student.getLastName() + " has reached " +
                            absenceCount + " total unexcused absences across all classes.",
                    student.getId(),
                    "User"
            );
        });
    }

    private void createNotification(User user, NotificationType type, String title, String message,
                                    Long relatedEntityId, String relatedEntityType) {
        // Create and save a notification
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setRelatedEntityType(relatedEntityType);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }
}