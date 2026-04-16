package dev.harakki.cdek_task_time_tracker.service;

import dev.harakki.cdek_task_time_tracker.domain.Task;
import dev.harakki.cdek_task_time_tracker.dto.TaskCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TaskPatchRequest;
import dev.harakki.cdek_task_time_tracker.dto.TaskResponse;
import dev.harakki.cdek_task_time_tracker.dto.TaskUpdateStatusRequest;
import dev.harakki.cdek_task_time_tracker.exception.ResourceNotFoundException;
import dev.harakki.cdek_task_time_tracker.mapper.TaskMapper;
import dev.harakki.cdek_task_time_tracker.mapstruct.mapper.TaskMapstructMapper;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskMapstructMapper taskMapstructMapper;

    public TaskResponse create(TaskCreateRequest request) {
        Task task = taskMapstructMapper.toTask(request);
        taskMapper.insert(task);
        return taskMapstructMapper.toTaskResponse(task);
    }

    public TaskResponse get(@NonNull Long taskId) {
        Task task = getTaskEntity(taskId);
        return taskMapstructMapper.toTaskResponse(task);
    }

    public TaskResponse update(@NonNull Long taskId, TaskPatchRequest request) {
        Task existingTask = getTaskEntity(taskId);
        taskMapstructMapper.partialUpdate(request, existingTask);
        taskMapper.updateStatus(existingTask.getId(), existingTask.getStatus());
        return taskMapstructMapper.toTaskResponse(existingTask);
    }

    public TaskResponse updateStatus(@NonNull Long taskId, TaskUpdateStatusRequest request) {
        Task existingTask = getTaskEntity(taskId);
        taskMapstructMapper.partialUpdate(request, existingTask);
        taskMapper.update(existingTask);
        return taskMapstructMapper.toTaskResponse(existingTask);
    }

    public void delete(@NonNull Long taskId) {
        getTaskEntity(taskId);
        taskMapper.delete(taskId);
    }

    public Task getTaskEntity(Long taskId) {
        return taskMapper.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + taskId + " not found"));
    }

}
