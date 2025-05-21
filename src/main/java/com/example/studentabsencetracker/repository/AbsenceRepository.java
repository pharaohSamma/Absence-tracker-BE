package com.example.studentabsencetracker.repository;

import com.example.studentabsencetracker.model.entity.Absence;
import com.example.studentabsencetracker.model.entity.CourseClass;
import com.example.studentabsencetracker.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByStudent(User student);

    List<Absence> findByStudentAndDateBetween(User student, LocalDate startDate, LocalDate endDate);

    List<Absence> findByCourseClass(CourseClass courseClass);

    List<Absence> findByCourseClassAndDate(CourseClass courseClass, LocalDate date);

    @Query("SELECT a FROM Absence a WHERE a.student.id = :studentId AND a.courseClass.id = :classId AND a.date = :date")
    List<Absence> findByStudentAndCourseClassAndDate(@Param("studentId") Long studentId, @Param("classId") Long classId, @Param("date") LocalDate date);

    List<Absence> findByDate(LocalDate date);

    // Fix for the error - using JPQL query to be explicit
    @Query("SELECT COUNT(a) FROM Absence a WHERE a.student.id = :studentId AND a.excused = false")
    long countUnexcusedAbsencesByStudent(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM Absence a WHERE a.student.id = :studentId AND a.courseClass.id = :classId AND a.excused = false")
    long countUnexcusedAbsencesByStudentAndClass(@Param("studentId") Long studentId, @Param("classId") Long classId);
}