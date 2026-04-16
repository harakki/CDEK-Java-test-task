package dev.harakki.cdek_task_time_tracker.dto;

import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;

/**
 * DTO for {@link dev.harakki.cdek_task_time_tracker.domain.Task}
 */
public record TaskPatchRequest(
        String name,
        String description,
        TaskStatus status
) {
}
