package dev.harakki.cdek_task_time_tracker.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * DTO for {@link dev.harakki.cdek_task_time_tracker.domain.TimeRecord}
 */
public record TimeRecordCreateRequest(
        @NotNull(message = "Employee ID must not be null")
        Long employeeId,
        @NotNull(message = "Task ID must not be null")
        Long taskId,
        @NotNull(message = "Start time must not be null")
        Instant startTime,
        @NotNull(message = "End time must not be null")
        Instant endTime,
        String workDescription
) {
}
