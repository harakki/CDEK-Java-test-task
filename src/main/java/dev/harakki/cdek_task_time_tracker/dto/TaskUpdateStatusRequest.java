package dev.harakki.cdek_task_time_tracker.dto;

import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link dev.harakki.cdek_task_time_tracker.domain.Task}
 */
public record TaskUpdateStatusRequest(
        @NotNull(message = "Status must not be null")
        TaskStatus status
) {
}
