package dev.harakki.cdek_task_time_tracker.controller;

import dev.harakki.cdek_task_time_tracker.dto.TaskCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TaskResponse;
import dev.harakki.cdek_task_time_tracker.dto.TaskUpdateStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;

@Tag(name = "Task", description = "Task management")
public interface TaskControllerApi {

    @Operation(summary = "Create task", description = "Creates a new task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    TaskResponse create(@RequestBody(description = "Create request") TaskCreateRequest request);

    @Operation(summary = "Get task by id", description = "Returns task by its ID")
    @ApiResponse(responseCode = "200", description = "Task found and returned")
    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    TaskResponse get(@Parameter(description = "Task ID") Long taskId);

    @Operation(summary = "Update task status", description = "Updates task status")
    @ApiResponse(responseCode = "200", description = "Status updated")
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    TaskResponse updateStatus(@Parameter(description = "Task ID") Long taskId,
                              @RequestBody(description = "Update request") TaskUpdateStatusRequest request);

}
