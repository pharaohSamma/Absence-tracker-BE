package com.example.studentabsencetracker.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JustificationStatistics {
    private long totalJustifications;
    private long pendingCount;
    private long approvedCount;
    private long rejectedCount;

    // Calculated fields
    public double getApprovalRate() {
        if (totalJustifications == 0) return 0.0;
        return (double) approvedCount / (approvedCount + rejectedCount) * 100;
    }

    public double getRejectionRate() {
        if (totalJustifications == 0) return 0.0;
        return (double) rejectedCount / (approvedCount + rejectedCount) * 100;
    }

    public long getProcessedCount() {
        return approvedCount + rejectedCount;
    }
}