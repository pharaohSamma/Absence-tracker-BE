package com.example.studentabsencetracker.model.dto.response;

import com.example.studentabsencetracker.model.entity.Absence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long classId;
    private String className;
    private LocalDate date;
    private String reason;
    private boolean excused;
    private Long recordedById;
    private String recordedByName;

    public AbsenceResponse(Absence absence) {
        this.id = absence.getId();
        this.studentId = absence.getStudent().getId();
        // Concatenate first name and last name instead of using getName()
        this.studentName = absence.getStudent().getFirstName() + " " + absence.getStudent().getLastName();
        this.classId = absence.getCourseClass().getId();
        this.className = absence.getCourseClass().getName();
        this.date = absence.getDate();
        this.reason = absence.getReason();
        this.excused = absence.isExcused();

        // Handle the recorded by user
        if (absence.getRecordedBy() != null) {
            this.recordedById = absence.getRecordedBy().getId();
            this.recordedByName = absence.getRecordedBy().getFirstName() + " " + absence.getRecordedBy().getLastName();
        }
    }
}