// ClassResponse.java
package com.example.studentabsencetracker.model.dto.response;

import com.example.studentabsencetracker.model.entity.CourseClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponse {
    private Long id;
    private String classCode;
    private String name;
    private String description;
    private UserResponse teacher;
    private List<UserResponse> students;
    private LocalDateTime createdAt;

    public ClassResponse(CourseClass courseClassEntity) {
        this.id = courseClassEntity.getId();
        this.classCode = courseClassEntity.getClassCode();
        this.name = courseClassEntity.getName();
        this.description = courseClassEntity.getDescription();
        this.createdAt = courseClassEntity.getCreatedAt();

        if (courseClassEntity.getTeacher() != null) {
            this.teacher = new UserResponse(courseClassEntity.getTeacher());
        }

        if (courseClassEntity.getStudents() != null && !courseClassEntity.getStudents().isEmpty()) {
            this.students = courseClassEntity.getStudents().stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());
        }
    }
}