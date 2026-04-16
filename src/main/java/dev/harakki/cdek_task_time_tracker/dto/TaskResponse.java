package dev.harakki.cdek_task_time_tracker.dto;

import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;

import java.time.Instant;

/**
 * DTO for {@link dev.harakki.cdek_task_time_tracker.domain.Task}
 */
public record TaskResponse(
        Long id,
        String name,
        String description,
        TaskStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
