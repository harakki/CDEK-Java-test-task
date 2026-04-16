package dev.harakki.cdek_task_time_tracker.service;

import dev.harakki.cdek_task_time_tracker.domain.Task;
import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;
import dev.harakki.cdek_task_time_tracker.dto.TaskCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TaskPatchRequest;
import dev.harakki.cdek_task_time_tracker.dto.TaskResponse;
import dev.harakki.cdek_task_time_tracker.dto.TaskUpdateStatusRequest;
import dev.harakki.cdek_task_time_tracker.exception.ResourceNotFoundException;
import dev.harakki.cdek_task_time_tracker.mapper.TaskMapper;
import dev.harakki.cdek_task_time_tracker.mapstruct.mapper.TaskMapstructMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskMapstructMapper taskMapstructMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createShouldMapInsertAndReturnResponse() {
        var request = new TaskCreateRequest("Task", "Description", TaskStatus.NEW);
        var task = Task.builder()
                .name("Task")
                .description("Description")
                .status(TaskStatus.NEW)
                .build();
        var response = new TaskResponse(1L, "Task", "Description", TaskStatus.NEW, Instant.now(), Instant.now());

        when(taskMapstructMapper.toTask(request)).thenReturn(task);
        when(taskMapstructMapper.toTaskResponse(task)).thenReturn(response);

        var actual = taskService.create(request);

        assertEquals(response, actual);
        verify(taskMapper).insert(task);
    }

    @Test
    void getShouldReturnTaskWhenEntityExists() {
        var taskId = 10L;
        var task = Task.builder()
                .id(taskId)
                .name("Task")
                .status(TaskStatus.IN_PROGRESS)
                .build();
        var response = new TaskResponse(taskId, "Task", "Description", TaskStatus.IN_PROGRESS, Instant.now(), Instant.now());

        when(taskMapper.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapstructMapper.toTaskResponse(task)).thenReturn(response);

        var actual = taskService.get(taskId);

        assertEquals(response, actual);
    }

    @Test
    void getShouldThrowWhenEntityNotFound() {
        var taskId = 11L;
        when(taskMapper.findById(taskId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> taskService.get(taskId));

        assertEquals("Task with ID 11 not found", exception.getMessage());
    }

    @Test
    void updateShouldPatchTaskAndCallUpdateStatus() {
        var taskId = 15L;
        var existingTask = Task.builder()
                .id(taskId)
                .name("Old")
                .description("Old description")
                .status(TaskStatus.NEW)
                .build();
        var request = new TaskPatchRequest("New", "New description", TaskStatus.IN_PROGRESS);
        var response = new TaskResponse(taskId, "New", "New description", TaskStatus.IN_PROGRESS, Instant.now(), Instant.now());

        when(taskMapper.findById(taskId)).thenReturn(Optional.of(existingTask));
        doAnswer(invocation -> {
            TaskPatchRequest patch = invocation.getArgument(0);
            Task target = invocation.getArgument(1);
            target.setName(patch.name());
            target.setDescription(patch.description());
            target.setStatus(patch.status());
            return null;
        }).when(taskMapstructMapper).partialUpdate(any(TaskPatchRequest.class), any(Task.class));
        when(taskMapstructMapper.toTaskResponse(existingTask)).thenReturn(response);

        var actual = taskService.update(taskId, request);

        assertEquals(response, actual);
        verify(taskMapper).updateStatus(taskId, TaskStatus.IN_PROGRESS);
        verify(taskMapper, never()).update(any(Task.class));
    }

    @Test
    void updateStatusShouldPatchStatusAndCallUpdate() {
        var taskId = 16L;
        var existingTask = Task.builder()
                .id(taskId)
                .name("Task")
                .description("Description")
                .status(TaskStatus.NEW)
                .build();
        var request = new TaskUpdateStatusRequest(TaskStatus.DONE);
        var response = new TaskResponse(taskId, "Task", "Description", TaskStatus.DONE, Instant.now(), Instant.now());

        when(taskMapper.findById(taskId)).thenReturn(Optional.of(existingTask));
        doAnswer(invocation -> {
            TaskUpdateStatusRequest patch = invocation.getArgument(0);
            Task target = invocation.getArgument(1);
            target.setStatus(patch.status());
            return null;
        }).when(taskMapstructMapper).partialUpdate(any(TaskUpdateStatusRequest.class), any(Task.class));
        when(taskMapstructMapper.toTaskResponse(existingTask)).thenReturn(response);

        var actual = taskService.updateStatus(taskId, request);

        assertEquals(response, actual);
        verify(taskMapper).update(existingTask);
        verify(taskMapper, never()).updateStatus(any(Long.class), any(TaskStatus.class));
    }

    @Test
    void deleteShouldCallMapperWhenTaskExists() {
        var taskId = 22L;
        var existingTask = Task.builder().id(taskId).name("Task").status(TaskStatus.NEW).build();
        when(taskMapper.findById(taskId)).thenReturn(Optional.of(existingTask));

        taskService.delete(taskId);

        verify(taskMapper).delete(taskId);
    }

    @Test
    void deleteShouldThrowWhenTaskNotFound() {
        var taskId = 23L;
        when(taskMapper.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.delete(taskId));
        verify(taskMapper, never()).delete(any(Long.class));
    }

}
