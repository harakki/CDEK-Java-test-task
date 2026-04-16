package dev.harakki.cdek_task_time_tracker.controller;

import dev.harakki.cdek_task_time_tracker.dto.TaskCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TaskResponse;
import dev.harakki.cdek_task_time_tracker.dto.TaskUpdateStatusRequest;
import dev.harakki.cdek_task_time_tracker.service.TaskService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tasks")
class TaskController implements TaskControllerApi {

    private final TaskService taskService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@RequestBody @Valid TaskCreateRequest request) {
        return taskService.create(request);
    }

    @Override
    @GetMapping("{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse get(@PathVariable @NonNull Long taskId) {
        return taskService.get(taskId);
    }

    // Сделал эндпоинты недоступными, так как по ТЗ их просто нет
    /*
    @Override
    @PatchMapping("{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse update(@PathVariable @NonNull Long taskId, @RequestBody @Valid TaskPatchRequest request) {
        return taskService.update(taskId, request);
    }
    */

    @Override
    @PatchMapping("{taskId}/status")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse updateStatus(@PathVariable @NonNull Long taskId,
                                     @RequestBody @Valid TaskUpdateStatusRequest request) {
        return taskService.updateStatus(taskId, request);
    }

    /*
    @Override
    @DeleteMapping("{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NonNull Long taskId) {
        taskService.delete(taskId);
    }
    */

}
