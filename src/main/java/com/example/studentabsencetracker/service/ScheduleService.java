package com.example.studentabsencetracker.service;

import com.example.studentabsencetracker.model.entity.CourseClass;
import com.example.studentabsencetracker.model.entity.Schedule;
import com.example.studentabsencetracker.model.enums.DayOfWeek;
import com.example.studentabsencetracker.repository.ClassRepository;
import com.example.studentabsencetracker.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ClassRepository classRepository;

    /**
     * Check if a class is scheduled for a specific day of the week
     */
    public boolean isClassScheduledForDay(Long classId, java.time.DayOfWeek dayOfWeek) {
        // Convert Java DayOfWeek to our custom DayOfWeek enum
        DayOfWeek customDayOfWeek = convertDayOfWeek(dayOfWeek);

        // Find schedules for the class on that day
        List<Schedule> schedules = scheduleRepository.findByCourseClassIdAndDayOfWeek(classId, customDayOfWeek);
        return !schedules.isEmpty();
    }

    /**
     * Convert java.time.DayOfWeek to our custom DayOfWeek enum
     */
    private DayOfWeek convertDayOfWeek(java.time.DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return DayOfWeek.MONDAY;
            case TUESDAY: return DayOfWeek.TUESDAY;
            case WEDNESDAY: return DayOfWeek.WEDNESDAY;
            case THURSDAY: return DayOfWeek.THURSDAY;
            case FRIDAY: return DayOfWeek.FRIDAY;
            case SATURDAY: return DayOfWeek.SATURDAY;
            case SUNDAY: return DayOfWeek.SUNDAY;
            default: throw new IllegalArgumentException("Unknown day of week: " + dayOfWeek);
        }
    }

    /**
     * Create a schedule for a class
     */
    public Schedule createSchedule(Long classId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, String location) {
        CourseClass courseClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));

        Schedule schedule = new Schedule();
        schedule.setCourseClass(courseClass);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setLocation(location);

        return scheduleRepository.save(schedule);
    }

    /**
     * Get all schedules for a class
     */
    public List<Schedule> getSchedulesByClass(Long classId) {
        return scheduleRepository.findByCourseClassId(classId);
    }
}