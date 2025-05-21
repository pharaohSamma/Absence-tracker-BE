package com.example.studentabsencetracker.repository;

import com.example.studentabsencetracker.model.entity.Schedule;
import com.example.studentabsencetracker.model.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByCourseClassId(Long classId);
    List<Schedule> findByCourseClassIdAndDayOfWeek(Long classId, DayOfWeek dayOfWeek);
}