package dev.harakki.cdek_task_time_tracker.mapstruct.mapper;

import dev.harakki.cdek_task_time_tracker.domain.TimeRecord;
import dev.harakki.cdek_task_time_tracker.dto.TimeRecordCreateRequest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TimeRecordMapstructMapperTest {

    private final TimeRecordMapstructMapper mapper = Mappers.getMapper(TimeRecordMapstructMapper.class);

    @Test
    void toTimeRecordShouldMapRequestAndIgnoreSystemFields() {
        var start = Instant.parse("2026-04-15T10:00:00Z");
        var end = Instant.parse("2026-04-15T11:00:00Z");
        var request = new TimeRecordCreateRequest(10L, 20L, start, end, "Investigated bug");

        var mapped = mapper.toTimeRecord(request);

        assertEquals(10L, mapped.getEmployeeId());
        assertEquals(20L, mapped.getTaskId());
        assertEquals(start, mapped.getStartTime());
        assertEquals(end, mapped.getEndTime());
        assertEquals("Investigated bug", mapped.getWorkDescription());
        assertNull(mapped.getId());
        assertNull(mapped.getCreatedAt());
        assertNull(mapped.getUpdatedAt());
        assertNull(mapped.getIsDeleted());
    }

    @Test
    void toTimeRecordResponseShouldMapAllFields() {
        var start = Instant.parse("2026-04-15T10:00:00Z");
        var end = Instant.parse("2026-04-15T11:00:00Z");
        var createdAt = Instant.parse("2026-04-15T11:10:00Z");
        var updatedAt = Instant.parse("2026-04-15T11:20:00Z");
        var record = TimeRecord.builder()
                .id(55L)
                .employeeId(10L)
                .taskId(20L)
                .startTime(start)
                .endTime(end)
                .workDescription("Investigated bug")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .isDeleted(false)
                .build();

        var response = mapper.toTimeRecordResponse(record);

        assertEquals(55L, response.id());
        assertEquals(10L, response.employeeId());
        assertEquals(20L, response.taskId());
        assertEquals(start, response.startTime());
        assertEquals(end, response.endTime());
        assertEquals("Investigated bug", response.workDescription());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
    }

}
