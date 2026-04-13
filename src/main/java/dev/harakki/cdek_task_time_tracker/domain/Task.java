package dev.harakki.cdek_task_time_tracker.domain;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Task {

    private Long id;

    private String name;

    private String description;

    private TaskStatus status;

    private Instant createdAt;

    private Instant updatedAt;

}
