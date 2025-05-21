package com.example.studentabsencetracker.service;

import com.example.studentabsencetracker.exception.BadRequestException;
import com.example.studentabsencetracker.exception.ResourceNotFoundException;
import com.example.studentabsencetracker.model.dto.request.AbsenceRequest;
import com.example.studentabsencetracker.model.dto.response.AbsenceResponse;
import com.example.studentabsencetracker.model.entity.Absence;
import com.example.studentabsencetracker.model.entity.CourseClass;
import com.example.studentabsencetracker.model.entity.User;
import com.example.studentabsencetracker.repository.AbsenceRepository;
import com.example.studentabsencetracker.repository.ClassRepository;
import com.example.studentabsencetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AbsenceService {
    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ScheduleService scheduleService;

    public List<AbsenceResponse> getAllAbsences() {
        return absenceRepository.findAll().stream()
                .map(AbsenceResponse::new)
                .collect(Collectors.toList());
    }

    public AbsenceResponse getAbsenceById(Long id) {
        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Absence not found with id: " + id));
        return new AbsenceResponse(absence);
    }

    public List<AbsenceResponse> getAbsencesByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        return absenceRepository.findByStudent(student).stream()
                .map(AbsenceResponse::new)
                .collect(Collectors.toList());
    }

    public List<AbsenceResponse> getAbsencesByStudentAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate) {
        // Validate date range
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date must be before or equal to end date");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        return absenceRepository.findByStudentAndDateBetween(student, startDate, endDate).stream()
                .map(AbsenceResponse::new)
                .collect(Collectors.toList());
    }

    public List<AbsenceResponse> getAbsencesByClass(Long classId) {
        CourseClass courseClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        return absenceRepository.findByCourseClass(courseClass).stream()
                .map(AbsenceResponse::new)
                .collect(Collectors.toList());
    }

    public List<AbsenceResponse> getAbsencesByClassAndDate(Long classId, LocalDate date) {
        CourseClass courseClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        return absenceRepository.findByCourseClassAndDate(courseClass, date).stream()
                .map(AbsenceResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public AbsenceResponse createAbsence(AbsenceRequest absenceRequest) {
        // Validate the request
        validateAbsenceRequest(absenceRequest);

        User student = userRepository.findById(absenceRequest.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + absenceRequest.getStudentId()));

        CourseClass courseClass = classRepository.findById(absenceRequest.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + absenceRequest.getClassId()));

        // Validate that student is enrolled in the class
        if (!courseClass.getStudents().contains(student)) {
            throw new BadRequestException("Student is not enrolled in this class");
        }

        // Validate that the class is scheduled for the requested date
        validateClassScheduledForDate(courseClass, absenceRequest.getDate());

        // Check for duplicate absence
        if (!absenceRepository.findByStudentAndCourseClassAndDate(
                student.getId(), courseClass.getId(), absenceRequest.getDate()).isEmpty()) {
            throw new BadRequestException("Absence already recorded for this student, class, and date");
        }

        // Get current authenticated user as the one who recorded the absence
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User recordedBy = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));

        Absence absence = new Absence();
        absence.setStudent(student);
        absence.setCourseClass(courseClass);
        absence.setDate(absenceRequest.getDate());
        absence.setReason(absenceRequest.getReason());
        absence.setExcused(absenceRequest.isExcused());
        absence.setRecordedBy(recordedBy);

        absenceRepository.save(absence);

        // Send notifications
        sendAbsenceNotifications(absence);

        // Check absence thresholds
        checkAbsenceThresholds(student, courseClass);

        return new AbsenceResponse(absence);
    }

    @Transactional
    public List<AbsenceResponse> createAbsencesBatch(List<AbsenceRequest> absenceRequests) {
        if (absenceRequests == null || absenceRequests.isEmpty()) {
            throw new BadRequestException("Absence requests cannot be empty");
        }

        List<AbsenceResponse> responses = new ArrayList<>();

        for (AbsenceRequest request : absenceRequests) {
            try {
                responses.add(createAbsence(request));
            } catch (Exception e) {
                // Log the error but continue processing other requests
                // We could also collect errors and return them to the client
                System.err.println("Error processing absence request for student ID " +
                        request.getStudentId() + " and class ID " + request.getClassId() +
                        ": " + e.getMessage());
            }
        }

        return responses;
    }

    @Transactional
    public AbsenceResponse updateAbsence(Long id, AbsenceRequest absenceRequest) {
        // Validate the request
        validateAbsenceRequest(absenceRequest);

        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Absence not found with id: " + id));

        User student = userRepository.findById(absenceRequest.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + absenceRequest.getStudentId()));

        CourseClass courseClass = classRepository.findById(absenceRequest.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + absenceRequest.getClassId()));

        // Validate that student is enrolled in the class
        if (!courseClass.getStudents().contains(student)) {
            throw new BadRequestException("Student is not enrolled in this class");
        }

        // Validate that the class is scheduled for the requested date
        validateClassScheduledForDate(courseClass, absenceRequest.getDate());

        // Check for duplicate absence (excluding current absence)
        List<Absence> existingAbsences = absenceRepository.findByStudentAndCourseClassAndDate(
                student.getId(), courseClass.getId(), absenceRequest.getDate());

        existingAbsences.removeIf(a -> a.getId().equals(id));

        if (!existingAbsences.isEmpty()) {
            throw new BadRequestException("Another absence already recorded for this student, class, and date");
        }

        absence.setStudent(student);
        absence.setCourseClass(courseClass);
        absence.setDate(absenceRequest.getDate());
        absence.setReason(absenceRequest.getReason());
        absence.setExcused(absenceRequest.isExcused());

        absenceRepository.save(absence);

        return new AbsenceResponse(absence);
    }

    @Transactional
    public AbsenceResponse markAsExcused(Long id) {
        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Absence not found with id: " + id));

        absence.setExcused(true);
        absenceRepository.save(absence);

        // Notify the student that their absence has been excused
        notificationService.createAbsenceExcusedNotification(absence);

        return new AbsenceResponse(absence);
    }

    @Transactional
    public AbsenceResponse markAsUnexcused(Long id) {
        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Absence not found with id: " + id));

        absence.setExcused(false);
        absenceRepository.save(absence);

        // Check absence thresholds after marking as unexcused
        checkAbsenceThresholds(absence.getStudent(), absence.getCourseClass());

        return new AbsenceResponse(absence);
    }

    @Transactional
    public void deleteAbsence(Long id) {
        if (!absenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Absence not found with id: " + id);
        }
        absenceRepository.deleteById(id);
    }

    // Private helper methods for validation and business logic

    private void validateAbsenceRequest(AbsenceRequest request) {
        // Check if all required fields are present
        if (request.getStudentId() == null) {
            throw new BadRequestException("Student ID is required");
        }

        if (request.getClassId() == null) {
            throw new BadRequestException("Class ID is required");
        }

        if (request.getDate() == null) {
            throw new BadRequestException("Date is required");
        }

        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new BadRequestException("Reason is required");
        }

        // Date validation
        LocalDate today = LocalDate.now();
        if (request.getDate().isAfter(today)) {
            throw new BadRequestException("Absence date cannot be in the future");
        }
    }

    private void validateClassScheduledForDate(CourseClass courseClass, LocalDate date) {
        // Check if the class is scheduled for this date
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        boolean isScheduled = scheduleService.isClassScheduledForDay(courseClass.getId(), dayOfWeek);

        if (!isScheduled) {
            throw new BadRequestException("Class is not scheduled for this date: " + date);
        }
    }

    private void sendAbsenceNotifications(Absence absence) {
        // Notify the student about the recorded absence
        notificationService.createAbsenceRecordedNotification(absence);

        // Notify parents if the student is a minor
        User student = absence.getStudent();
        if (!student.getParents().isEmpty()) {
            notificationService.createParentAbsenceNotification(absence);
        }
    }

    private void checkAbsenceThresholds(User student, CourseClass courseClass) {
        // Check class-specific threshold
        long classAbsences = absenceRepository.countUnexcusedAbsencesByStudentAndClass(student.getId(), courseClass.getId());
        if (classAbsences >= 3) { // Assuming 3 is the threshold
            notificationService.createClassAbsenceThresholdNotification(student, courseClass, classAbsences);
        }

        // Check overall absence threshold
        long totalAbsences = absenceRepository.countUnexcusedAbsencesByStudent(student.getId());
        if (totalAbsences >= 10) { // Assuming 10 is the threshold
            notificationService.createOverallAbsenceThresholdNotification(student, totalAbsences);
        }
    }
}