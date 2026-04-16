package dev.harakki.cdek_task_time_tracker.mapper;

import dev.harakki.cdek_task_time_tracker.TestcontainersConfiguration;
import dev.harakki.cdek_task_time_tracker.domain.Task;
import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;
import dev.harakki.cdek_task_time_tracker.domain.TimeRecord;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest(properties = {
        "spring.docker.compose.enabled=false",
        "spring.sql.init.mode=always"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class TimeRecordMapperIntegrationTest {

    @Autowired
    private TimeRecordMapper timeRecordMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void insertAndFindByIdShouldPersistRecord() {
        var taskId = createTask("Task for record");

        var record = TimeRecord.builder()
                .employeeId(101L)
                .taskId(taskId)
                .startTime(Instant.parse("2026-04-15T10:00:00Z"))
                .endTime(Instant.parse("2026-04-15T11:00:00Z"))
                .workDescription("Worked on feature")
                .build();

        timeRecordMapper.insert(record);

        assertNotNull(record.getId());

        var fromDb = timeRecordMapper.findById(record.getId()).orElseThrow();
        assertEquals(101L, fromDb.getEmployeeId());
        assertEquals(taskId, fromDb.getTaskId());
        assertEquals(Instant.parse("2026-04-15T10:00:00Z"), fromDb.getStartTime());
        assertEquals(Instant.parse("2026-04-15T11:00:00Z"), fromDb.getEndTime());
        assertEquals("Worked on feature", fromDb.getWorkDescription());
        assertNotNull(fromDb.getCreatedAt());
        assertNotNull(fromDb.getUpdatedAt());
        assertNotEquals(Boolean.TRUE, fromDb.getIsDeleted());
    }

    @Test
    void findByEmployeeIdAndPeriodShouldReturnOnlyFullyIncludedRecords() {
        var taskId = createTask("Task period test");
        var employeeId = 200L;

        var fullyInside = createRecord(employeeId, taskId,
                "2026-04-15T10:10:00Z", "2026-04-15T11:10:00Z", "inside");
        createRecord(employeeId, taskId,
                "2026-04-15T09:50:00Z", "2026-04-15T10:20:00Z", "overlap-left");
        createRecord(employeeId, taskId,
                "2026-04-15T12:50:00Z", "2026-04-15T13:10:00Z", "overlap-right");
        createRecord(201L, taskId,
                "2026-04-15T10:30:00Z", "2026-04-15T10:50:00Z", "other-employee");

        var records = timeRecordMapper.findByEmployeeIdAndPeriod(
                employeeId,
                Instant.parse("2026-04-15T10:00:00Z"),
                Instant.parse("2026-04-15T13:00:00Z")
        );

        var ids = records.stream()
                .map(TimeRecord::getId)
                .collect(Collectors.toSet());
        assertEquals(Set.of(fullyInside.getId()), ids);
    }

    @Test
    void findByEmployeeIdAndSoftPeriodShouldReturnAllOverlappingRecords() {
        var taskId = createTask("Task soft period test");
        var employeeId = 300L;

        var inside = createRecord(employeeId, taskId,
                "2026-04-15T10:10:00Z", "2026-04-15T11:10:00Z", "inside");
        var overlapLeft = createRecord(employeeId, taskId,
                "2026-04-15T09:50:00Z", "2026-04-15T10:20:00Z", "overlap-left");
        var overlapRight = createRecord(employeeId, taskId,
                "2026-04-15T12:50:00Z", "2026-04-15T13:10:00Z", "overlap-right");
        createRecord(301L, taskId,
                "2026-04-15T10:20:00Z", "2026-04-15T10:40:00Z", "other-employee");

        var records = timeRecordMapper.findByEmployeeIdAndSoftPeriod(
                employeeId,
                Instant.parse("2026-04-15T10:00:00Z"),
                Instant.parse("2026-04-15T13:00:00Z")
        );

        var ids = records.stream().map(TimeRecord::getId).collect(Collectors.toSet());
        assertEquals(Set.of(inside.getId(), overlapLeft.getId(), overlapRight.getId()), ids);
    }

    @Test
    void updateShouldPersistAllMutableFields() {
        var taskId = createTask("Task update test");
        var taskId2 = createTask("Task update target");

        var record = createRecord(400L, taskId,
                "2026-04-15T10:00:00Z", "2026-04-15T11:00:00Z", "before");

        record.setEmployeeId(401L);
        record.setTaskId(taskId2);
        record.setStartTime(Instant.parse("2026-04-15T11:00:00Z"));
        record.setEndTime(Instant.parse("2026-04-15T12:30:00Z"));
        record.setWorkDescription("after");

        timeRecordMapper.update(record);

        TimeRecord updated = timeRecordMapper.findById(record.getId()).orElseThrow();
        assertEquals(401L, updated.getEmployeeId());
        assertEquals(taskId2, updated.getTaskId());
        assertEquals(Instant.parse("2026-04-15T11:00:00Z"), updated.getStartTime());
        assertEquals(Instant.parse("2026-04-15T12:30:00Z"), updated.getEndTime());
        assertEquals("after", updated.getWorkDescription());
    }

    @Test
    void deleteShouldSoftDeleteAndHideFromFindById() {
        var taskId = createTask("Task delete test");
        var record = createRecord(500L, taskId,
                "2026-04-15T10:00:00Z", "2026-04-15T11:00:00Z", "to-delete");

        timeRecordMapper.delete(record.getId());

        assertTrue(timeRecordMapper.findById(record.getId()).isEmpty());

        var isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM time_record WHERE id = ?",
                Boolean.class,
                record.getId()
        );
        assertEquals(Boolean.TRUE, isDeleted);
    }

    private Long createTask(String name) {
        var task = Task.builder()
                .name(name)
                .description("helper")
                .status(TaskStatus.NEW)
                .build();
        taskMapper.insert(task);
        return task.getId();
    }

    private TimeRecord createRecord(Long employeeId, Long taskId, String startIso, String endIso, String description) {
        var record = TimeRecord.builder()
                .employeeId(employeeId)
                .taskId(taskId)
                .startTime(Instant.parse(startIso))
                .endTime(Instant.parse(endIso))
                .workDescription(description)
                .build();
        timeRecordMapper.insert(record);
        return record;
    }

}
