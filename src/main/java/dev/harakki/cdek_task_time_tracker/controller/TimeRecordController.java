package dev.harakki.cdek_task_time_tracker.controller;

import dev.harakki.cdek_task_time_tracker.dto.EmployeeTimeReportResponse;
import dev.harakki.cdek_task_time_tracker.dto.TimeRecordCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TimeRecordResponse;
import dev.harakki.cdek_task_time_tracker.service.TimeRecordService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/time-records")
class TimeRecordController implements TimeRecordControllerApi {

    private final TimeRecordService timeRecordService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TimeRecordResponse create(@RequestBody @Valid TimeRecordCreateRequest request) {
        return timeRecordService.create(request);
    }

    @Override
    @GetMapping("{timeRecordId}")
    @ResponseStatus(HttpStatus.OK)
    public TimeRecordResponse get(@PathVariable @NonNull Long timeRecordId) {
        return timeRecordService.get(timeRecordId);
    }

    @Override
    @GetMapping("/employees/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeTimeReportResponse getEmployeeTimeForTasks(
            @PathVariable @NonNull Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
    ) {
        return timeRecordService.getEmployeeTimeForTasks(employeeId, startDate, endDate);
    }

}
