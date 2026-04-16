package dev.harakki.cdek_task_time_tracker.controller;

import dev.harakki.cdek_task_time_tracker.dto.EmployeeTimeReportResponse;
import dev.harakki.cdek_task_time_tracker.dto.TimeRecordCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TimeRecordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;

import java.time.Instant;

@Tag(name = "Time record", description = "Time record management")
public interface TimeRecordControllerApi {

    @Operation(summary = "Create time record", description = "Creates a new time record for task")
    @ApiResponse(responseCode = "201", description = "Time record created")
    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    TimeRecordResponse create(@RequestBody(description = "Create request") TimeRecordCreateRequest request);

    @Operation(summary = "Get time record by id", description = "Returns time record by its ID")
    @ApiResponse(responseCode = "200", description = "Time record found and returned")
    @ApiResponse(responseCode = "404", description = "Time record not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    TimeRecordResponse get(@Parameter(description = "Time record ID") Long timeRecordId);

    @Operation(summary = "Get the spent by employee on tasks", description = "Get total time spent by employee on any tasks")
    @ApiResponse(responseCode = "200", description = "Time spent by employee on tasks")
    @ApiResponse(responseCode = "400", description = "Invalid date format", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    EmployeeTimeReportResponse getEmployeeTimeForTasks(@Parameter(description = "Employee ID") Long employeeId,
                                                       @Parameter(description = "Start date of record") Instant startDate,
                                                       @Parameter(description = "End date of record") Instant endDate);

}
