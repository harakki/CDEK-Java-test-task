package dev.harakki.cdek_task_time_tracker.dto;

import java.time.Instant;

/**
 * DTO for {@link dev.harakki.cdek_task_time_tracker.domain.TimeRecord}
 */
public record TimeRecordResponse(
        Long id,
        Long employeeId,
        Long taskId,
        Instant startTime,
        Instant endTime,
        String workDescription,
        Instant createdAt,
        Instant updatedAt
) {
}
