package dev.harakki.cdek_task_time_tracker.domain;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TimeRecord {

    private Long id;

    private Long employeeId;

    private Long taskId;

    private Instant startTime;

    private Instant endTime;

    private String workDescription;

    private Instant createdAt;

    private Instant updatedAt;

}
