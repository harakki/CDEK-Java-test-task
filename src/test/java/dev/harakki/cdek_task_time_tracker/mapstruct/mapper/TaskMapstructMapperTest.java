package dev.harakki.cdek_task_time_tracker.mapstruct.mapper;

import dev.harakki.cdek_task_time_tracker.domain.Task;
import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;
import dev.harakki.cdek_task_time_tracker.dto.TaskCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TaskPatchRequest;
import dev.harakki.cdek_task_time_tracker.dto.TaskUpdateStatusRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskMapstructMapperTest {

    private final TaskMapstructMapper mapper = Mappers.getMapper(TaskMapstructMapper.class);

    @Test
    void toTaskShouldMapCreateRequestAndIgnoreSystemFields() {
        var request = new TaskCreateRequest("Task", "Description", TaskStatus.NEW);

        var task = mapper.toTask(request);

        assertEquals("Task", task.getName());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertNull(task.getId());
        assertNull(task.getCreatedAt());
        assertNull(task.getUpdatedAt());
        assertNull(task.getIsDeleted());
    }

    @Test
    void partialUpdateShouldIgnoreNullFields() {
        var task = Task.builder()
                .id(1L)
                .name("Original name")
                .description("Original description")
                .status(TaskStatus.IN_PROGRESS)
                .createdAt(Instant.parse("2026-04-01T08:00:00Z"))
                .updatedAt(Instant.parse("2026-04-01T09:00:00Z"))
                .isDeleted(false)
                .build();

        var request = new TaskPatchRequest(null, "Updated description", null);

        mapper.partialUpdate(request, task);

        assertEquals("Original name", task.getName());
        assertEquals("Updated description", task.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(1L, task.getId());
        assertEquals(false, task.getIsDeleted());
    }

    @Test
    void partialUpdateStatusShouldChangeOnlyStatus() {
        var task = Task.builder()
                .id(2L)
                .name("Task")
                .description("Description")
                .status(TaskStatus.NEW)
                .createdAt(Instant.parse("2026-04-01T08:00:00Z"))
                .updatedAt(Instant.parse("2026-04-01T09:00:00Z"))
                .isDeleted(false)
                .build();

        mapper.partialUpdate(new TaskUpdateStatusRequest(TaskStatus.DONE), task);

        assertEquals("Task", task.getName());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals(2L, task.getId());
        assertEquals(false, task.getIsDeleted());
    }

    @Test
    void toTaskResponseShouldMapFields() {
        var createdAt = Instant.parse("2026-04-10T07:00:00Z");
        var updatedAt = Instant.parse("2026-04-10T08:00:00Z");
        var task = Task.builder()
                .id(99L)
                .name("Task")
                .description("Description")
                .status(TaskStatus.IN_PROGRESS)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .isDeleted(false)
                .build();

        var response = mapper.toTaskResponse(task);

        assertEquals(99L, response.id());
        assertEquals("Task", response.name());
        assertEquals("Description", response.description());
        assertEquals(TaskStatus.IN_PROGRESS, response.status());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
    }

}
