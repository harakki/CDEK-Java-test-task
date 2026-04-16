package dev.harakki.cdek_task_time_tracker.dto;

import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link dev.harakki.cdek_task_time_tracker.domain.Task}
 */
public record TaskCreateRequest(
        @NotBlank(message = "Name must not be null and blank")
        String name,
        String description,
        @NotNull(message = "Status must not be null")
        TaskStatus status
) {
}
