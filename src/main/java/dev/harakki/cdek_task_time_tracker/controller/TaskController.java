package dev.harakki.cdek_task_time_tracker.controller;

import dev.harakki.cdek_task_time_tracker.domain.Task;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task")
class TaskController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@RequestBody Task task) {
    }

    @GetMapping("{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public Task get(@PathVariable("taskId") Long taskId) {
    }

    @PatchMapping("{taskId}/status")
    @ResponseStatus(HttpStatus.OK)
    public Task updateStatus(@PathVariable("taskId") Long taskId, @RequestParam("status") String status) {
    }

}
