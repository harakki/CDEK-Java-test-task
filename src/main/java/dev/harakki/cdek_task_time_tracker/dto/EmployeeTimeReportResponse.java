package dev.harakki.cdek_task_time_tracker.dto;

import java.util.List;

public record EmployeeTimeReportResponse(
        Long employeeId,
        Long totalTimeSpentSeconds,
        List<TimeRecordResponse> records
) {
}
