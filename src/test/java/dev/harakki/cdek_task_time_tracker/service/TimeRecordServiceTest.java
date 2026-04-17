package dev.harakki.cdek_task_time_tracker.service;

import dev.harakki.cdek_task_time_tracker.domain.Task;
import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;
import dev.harakki.cdek_task_time_tracker.domain.TimeRecord;
import dev.harakki.cdek_task_time_tracker.dto.EmployeeTimeReportResponse;
import dev.harakki.cdek_task_time_tracker.dto.TimeRecordCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TimeRecordResponse;
import dev.harakki.cdek_task_time_tracker.exception.ResourceNotFoundException;
import dev.harakki.cdek_task_time_tracker.mapper.TimeRecordMapper;
import dev.harakki.cdek_task_time_tracker.mapstruct.mapper.TimeRecordMapstructMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeRecordServiceTest {

    @Mock
    private TimeRecordMapper timeRecordMapper;

    @Mock
    private TimeRecordMapstructMapper mapstructMapper;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TimeRecordService timeRecordService;

    @Test
    void createShouldValidateTaskAndPersistRecord() {
        var start = Instant.parse("2026-04-15T10:00:00Z");
        var end = Instant.parse("2026-04-15T11:00:00Z");

        var request = new TimeRecordCreateRequest(5L, 7L, start, end, "Work");
        var timeRecord = TimeRecord.builder().employeeId(5L).taskId(7L).startTime(start).endTime(end).workDescription("Work").build();
        var response = new TimeRecordResponse(1L, 5L, 7L, start, end, "Work", Instant.now(), Instant.now());

        when(taskService.getTaskEntity(7L)).thenReturn(Task.builder().id(7L).status(TaskStatus.NEW).build());
        when(mapstructMapper.toTimeRecord(request)).thenReturn(timeRecord);
        when(mapstructMapper.toTimeRecordResponse(timeRecord)).thenReturn(response);

        var actual = timeRecordService.create(request);

        assertEquals(response, actual);
        verify(taskService).getTaskEntity(7L);
        verify(timeRecordMapper).insert(timeRecord);
    }

    @Test
    void createShouldThrowWhenEndBeforeStart() {
        var start = Instant.parse("2026-04-15T12:00:00Z");
        var end = Instant.parse("2026-04-15T11:00:00Z");
        var request = new TimeRecordCreateRequest(5L, 7L, start, end, "Work");

        var exception = assertThrows(IllegalArgumentException.class, () -> timeRecordService.create(request));

        assertEquals("End time must be after start time", exception.getMessage());
        verifyNoInteractions(taskService, timeRecordMapper, mapstructMapper);
    }

    @Test
    void createShouldThrowWhenEndEqualsStart() {
        var start = Instant.parse("2026-04-15T12:00:00Z");
        var end = Instant.parse("2026-04-15T12:00:00Z");
        var request = new TimeRecordCreateRequest(5L, 7L, start, end, "Work");

        var exception = assertThrows(IllegalArgumentException.class, () -> timeRecordService.create(request));

        assertEquals("End time must be after start time", exception.getMessage());
        verifyNoInteractions(taskService, timeRecordMapper, mapstructMapper);
    }

    @Test
    void getShouldReturnMappedResponseWhenRecordExists() {
        var recordId = 101L;
        var record = TimeRecord.builder()
                .id(recordId)
                .employeeId(5L)
                .taskId(10L)
                .build();
        var response = new TimeRecordResponse(recordId, 5L, 10L, Instant.now(), Instant.now(), "Work", Instant.now(), Instant.now());

        when(timeRecordMapper.findById(recordId)).thenReturn(Optional.of(record));
        when(mapstructMapper.toTimeRecordResponse(record)).thenReturn(response);

        var actual = timeRecordService.get(recordId);

        assertEquals(response, actual);
    }

    @Test
    void getShouldThrowWhenRecordNotFound() {
        var recordId = 102L;
        when(timeRecordMapper.findById(recordId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> timeRecordService.get(recordId));

        assertEquals("Time record with ID 102 not found", exception.getMessage());
    }

    @Test
    void getEmployeeTimeForTasksShouldThrowWhenEndBeforeStart() {
        var employeeId = 17L;
        var start = Instant.parse("2026-04-15T13:00:00Z");
        var end = Instant.parse("2026-04-15T10:00:00Z");

        var exception = assertThrows(IllegalArgumentException.class, () -> timeRecordService.getEmployeeTimeForTasks(employeeId, start, end));

        assertEquals("Start date cannot be after end date", exception.getMessage());
        verify(timeRecordMapper, never()).findByEmployeeIdAndSoftPeriod(any(Long.class), any(Instant.class), any(Instant.class));
    }

    @Test
    void getEmployeeTimeForTasksShouldCalculateOverlapAndMapRecords() {
        var employeeId = 8L;
        var reportStart = Instant.parse("2026-04-15T10:00:00Z");
        var reportEnd = Instant.parse("2026-04-15T14:00:00Z");

        var first = TimeRecord.builder()
                .id(1L)
                .employeeId(employeeId)
                .taskId(100L)
                .startTime(Instant.parse("2026-04-15T09:30:00Z"))
                .endTime(Instant.parse("2026-04-15T10:30:00Z"))
                .workDescription("A")
                .build();

        var second = TimeRecord.builder()
                .id(2L)
                .employeeId(employeeId)
                .taskId(101L)
                .startTime(Instant.parse("2026-04-15T11:00:00Z"))
                .endTime(Instant.parse("2026-04-15T12:15:00Z"))
                .workDescription("B")
                .build();

        var third = TimeRecord.builder()
                .id(3L)
                .employeeId(employeeId)
                .taskId(102L)
                .startTime(Instant.parse("2026-04-15T13:45:00Z"))
                .endTime(Instant.parse("2026-04-15T14:30:00Z"))
                .workDescription("C")
                .build();

        var records = List.of(first, second, third);

        var firstResponse = new TimeRecordResponse(1L, employeeId, 100L, first.getStartTime(), first.getEndTime(), "A", Instant.now(), Instant.now());
        var secondResponse = new TimeRecordResponse(2L, employeeId, 101L, second.getStartTime(), second.getEndTime(), "B", Instant.now(), Instant.now());
        var thirdResponse = new TimeRecordResponse(3L, employeeId, 102L, third.getStartTime(), third.getEndTime(), "C", Instant.now(), Instant.now());

        when(timeRecordMapper.findByEmployeeIdAndSoftPeriod(employeeId, reportStart, reportEnd)).thenReturn(records);
        when(mapstructMapper.toTimeRecordResponse(first)).thenReturn(firstResponse);
        when(mapstructMapper.toTimeRecordResponse(second)).thenReturn(secondResponse);
        when(mapstructMapper.toTimeRecordResponse(third)).thenReturn(thirdResponse);

        EmployeeTimeReportResponse actual = timeRecordService.getEmployeeTimeForTasks(employeeId, reportStart, reportEnd);

        assertEquals(employeeId, actual.employeeId());
        assertEquals(7200L, actual.totalTimeSpentSeconds());
        assertEquals(List.of(firstResponse, secondResponse, thirdResponse), actual.records());
    }

    @Test
    void getTimeRecordEntityShouldThrowWhenNotFound() {
        var recordId = 300L;
        when(timeRecordMapper.findById(recordId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> timeRecordService.getTimeRecordEntity(recordId));
    }

}
