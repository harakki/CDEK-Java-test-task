package dev.harakki.cdek_task_time_tracker.controller;

import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;
import dev.harakki.cdek_task_time_tracker.dto.TaskCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TaskResponse;
import dev.harakki.cdek_task_time_tracker.dto.TaskUpdateStatusRequest;
import dev.harakki.cdek_task_time_tracker.exception.ResourceNotFoundException;
import dev.harakki.cdek_task_time_tracker.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void createShouldReturnCreatedTask() throws Exception {
        var response = new TaskResponse(
                1L,
                "Task",
                "Description",
                TaskStatus.NEW,
                Instant.parse("2026-04-16T10:00:00Z"),
                Instant.parse("2026-04-16T10:00:00Z")
        );

        when(taskService.create(any(TaskCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Task",
                                  "description": "Description",
                                  "status": "NEW"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Task"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void createShouldReturnBadRequestWhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "description": "Description"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }

    @Test
    void getShouldReturnTask() throws Exception {
        var response = new TaskResponse(
                7L,
                "Task",
                "Description",
                TaskStatus.IN_PROGRESS,
                Instant.parse("2026-04-16T10:00:00Z"),
                Instant.parse("2026-04-16T11:00:00Z")
        );

        when(taskService.get(7L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/tasks/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void getShouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        when(taskService.get(999L)).thenThrow(new ResourceNotFoundException("Task with ID 999 not found"));

        mockMvc.perform(get("/api/v1/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Task with ID 999 not found"));
    }

    @Test
    void updateStatusShouldReturnUpdatedTask() throws Exception {
        var response = new TaskResponse(
                3L,
                "Task",
                "Description",
                TaskStatus.DONE,
                Instant.parse("2026-04-16T10:00:00Z"),
                Instant.parse("2026-04-16T12:00:00Z")
        );

        when(taskService.updateStatus(eq(3L), any(TaskUpdateStatusRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/tasks/3/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "DONE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void updateStatusShouldReturnBadRequestWhenEnumInvalid() throws Exception {
        mockMvc.perform(patch("/api/v1/tasks/3/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "INVALID"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Failed to read request"));
    }

    @Test
    void getShouldReturnBadRequestWhenTaskIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Failed to convert 'taskId' with value: 'abc'"));
    }

}
