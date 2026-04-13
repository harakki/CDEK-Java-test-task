package dev.harakki.cdek_task_time_tracker.controller.advice;

import dev.harakki.cdek_task_time_tracker.domain.TimeRecord;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/time-record")
class TimeRecordController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TimeRecord create(@RequestBody TimeRecord timeRecord) {
    }

    @GetMapping("{timeRecordId}")
    @ResponseStatus(HttpStatus.OK)
    public TimeRecord get(@PathVariable("timeRecordId") Long timeRecordId) {
    }

    @GetMapping("/employee/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TimeRecord> getByEmployeeAndPeriod(@PathVariable("employeeId") Long employeeId,
                                                   @RequestParam("startDate") Instant startDate,
                                                   @RequestParam("endDate") Instant endDate) {
    }

}
