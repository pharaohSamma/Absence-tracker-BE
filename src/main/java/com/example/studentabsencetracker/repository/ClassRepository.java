package com.example.studentabsencetracker.repository;

import com.example.studentabsencetracker.model.entity.CourseClass;
import com.example.studentabsencetracker.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<CourseClass, Long> {
    Optional<CourseClass> findByClassCode(String classCode);
    boolean existsByClassCode(String classCode);
    List<CourseClass> findByTeacher(User teacher);
    List<CourseClass> findByStudentsContaining(User student);
}