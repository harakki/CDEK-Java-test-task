package dev.harakki.cdek_task_time_tracker.controller;

import dev.harakki.cdek_task_time_tracker.dto.EmployeeTimeReportResponse;
import dev.harakki.cdek_task_time_tracker.dto.TimeRecordCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TimeRecordResponse;
import dev.harakki.cdek_task_time_tracker.service.TimeRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimeRecordController.class)
class TimeRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimeRecordService timeRecordService;

    @Test
    void createShouldReturnCreatedTimeRecord() throws Exception {
        var start = Instant.parse("2026-04-16T09:00:00Z");
        var end = Instant.parse("2026-04-16T10:00:00Z");

        var response = new TimeRecordResponse(
                11L,
                77L,
                5L,
                start,
                end,
                "Implemented tests",
                Instant.parse("2026-04-16T10:01:00Z"),
                Instant.parse("2026-04-16T10:01:00Z")
        );

        when(timeRecordService.create(any(TimeRecordCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/time-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "employeeId": 77,
                                  "taskId": 5,
                                  "startTime": "2026-04-16T09:00:00Z",
                                  "endTime": "2026-04-16T10:00:00Z",
                                  "workDescription": "Implemented tests"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.employeeId").value(77))
                .andExpect(jsonPath("$.taskId").value(5));
    }

    @Test
    void createShouldReturnBadRequestWhenValidationFails() throws Exception {
        mockMvc.perform(post("/api/v1/time-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "taskId": 5,
                                  "startTime": "2026-04-16T09:00:00Z",
                                  "endTime": "2026-04-16T10:00:00Z"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request content."));
    }

    @Test
    void getShouldReturnTimeRecord() throws Exception {
        var response = new TimeRecordResponse(
                15L,
                77L,
                5L,
                Instant.parse("2026-04-16T09:00:00Z"),
                Instant.parse("2026-04-16T10:00:00Z"),
                "Implemented tests",
                Instant.parse("2026-04-16T10:01:00Z"),
                Instant.parse("2026-04-16T10:01:00Z")
        );

        when(timeRecordService.get(15L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/time-records/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.workDescription").value("Implemented tests"));
    }

    @Test
    void getEmployeeTimeForTasksShouldReturnReport() throws Exception {
        var item = new TimeRecordResponse(
                1L,
                10L,
                20L,
                Instant.parse("2026-04-16T09:00:00Z"),
                Instant.parse("2026-04-16T10:00:00Z"),
                "Work",
                Instant.parse("2026-04-16T10:00:00Z"),
                Instant.parse("2026-04-16T10:00:00Z")
        );
        var response = new EmployeeTimeReportResponse(10L, 3600L, List.of(item));

        when(timeRecordService.getEmployeeTimeForTasks(
                eq(10L),
                eq(Instant.parse("2026-04-16T08:00:00Z")),
                eq(Instant.parse("2026-04-16T18:00:00Z"))
        )).thenReturn(response);

        mockMvc.perform(get("/api/v1/time-records/employees/10")
                        .param("startDate", "2026-04-16T08:00:00Z")
                        .param("endDate", "2026-04-16T18:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(10))
                .andExpect(jsonPath("$.totalTimeSpentSeconds").value(3600))
                .andExpect(jsonPath("$.records[0].id").value(1));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidDateRequestCases")
    void getEmployeeTimeForTasksShouldReturnBadRequestForInvalidDateRequests(
            String displayName,
            String startDate,
            String endDate,
            String expectedDetail
    ) throws Exception {
        var request = get("/api/v1/time-records/employees/10");

        if (startDate != null) {
            request.param("startDate", startDate);
        }
        if (endDate != null) {
            request.param("endDate", endDate);
        }

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value(expectedDetail));
    }

    @Test
    void getEmployeeTimeForTasksShouldReturnBadRequestWhenServiceRejectsRange() throws Exception {
        when(timeRecordService.getEmployeeTimeForTasks(
                eq(10L),
                eq(Instant.parse("2026-04-16T18:00:00Z")),
                eq(Instant.parse("2026-04-16T08:00:00Z"))
        )).thenThrow(new IllegalArgumentException("Start date cannot be after end date"));

        mockMvc.perform(get("/api/v1/time-records/employees/10")
                        .param("startDate", "2026-04-16T18:00:00Z")
                        .param("endDate", "2026-04-16T08:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Start date cannot be after end date"));
    }

    private static Stream<Arguments> invalidDateRequestCases() {
        return Stream.of(
                Arguments.of(
                        "missing endDate",
                        "2026-04-16T08:00:00Z",
                        null,
                        "Required parameter 'endDate' is not present."
                ),
                Arguments.of(
                        "missing startDate",
                        null,
                        "2026-04-16T18:00:00Z",
                        "Required parameter 'startDate' is not present."
                ),
                Arguments.of(
                        "invalid startDate format",
                        "2026/04/16 08:00:00",
                        "2026-04-16T18:00:00Z",
                        "Failed to convert 'startDate' with value: '2026/04/16 08:00:00'"
                ),
                Arguments.of(
                        "invalid endDate format",
                        "2026-04-16T08:00:00Z",
                        "not-a-date",
                        "Failed to convert 'endDate' with value: 'not-a-date'"
                )
        );
    }

}
