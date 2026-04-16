package dev.harakki.cdek_task_time_tracker.mapper;

import dev.harakki.cdek_task_time_tracker.TestcontainersConfiguration;
import dev.harakki.cdek_task_time_tracker.domain.Task;
import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest(properties = {
        "spring.docker.compose.enabled=false",
        "spring.sql.init.mode=always"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class TaskMapperIntegrationTest {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void insertAndFindByIdShouldPersistTask() {
        var task = Task.builder()
                .name("Create API")
                .description("Implement create endpoint")
                .status(TaskStatus.NEW)
                .build();

        taskMapper.insert(task);

        assertNotNull(task.getId());

        var fromDb = taskMapper.findById(task.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Create API", fromDb.get().getName());
        assertEquals("Implement create endpoint", fromDb.get().getDescription());
        assertEquals(TaskStatus.NEW, fromDb.get().getStatus());
        assertNotNull(fromDb.get().getCreatedAt());
        assertNotNull(fromDb.get().getUpdatedAt());
        assertNotEquals(Boolean.TRUE, fromDb.get().getIsDeleted());
    }

    @Test
    void updateShouldPersistAllMutableFields() {
        var task = Task.builder()
                .name("Original")
                .description("Original description")
                .status(TaskStatus.NEW)
                .build();
        taskMapper.insert(task);

        task.setName("Updated");
        task.setDescription("Updated description");
        task.setStatus(TaskStatus.IN_PROGRESS);

        taskMapper.update(task);

        var updated = taskMapper.findById(task.getId()).orElseThrow();
        assertEquals("Updated", updated.getName());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());
    }

    @Test
    void updateStatusShouldChangeOnlyStatus() {
        var task = Task.builder()
                .name("Task")
                .description("Description")
                .status(TaskStatus.NEW)
                .build();
        taskMapper.insert(task);

        taskMapper.updateStatus(task.getId(), TaskStatus.DONE);

        var updated = taskMapper.findById(task.getId()).orElseThrow();
        assertEquals("Task", updated.getName());
        assertEquals("Description", updated.getDescription());
        assertEquals(TaskStatus.DONE, updated.getStatus());
    }

    @Test
    void deleteShouldSoftDeleteAndHideFromFindById() {
        var task = Task.builder()
                .name("Delete me")
                .description("Soft delete check")
                .status(TaskStatus.NEW)
                .build();
        taskMapper.insert(task);

        taskMapper.delete(task.getId());

        assertTrue(taskMapper.findById(task.getId()).isEmpty());

        var isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM task WHERE id = ?",
                Boolean.class,
                task.getId()
        );
        assertEquals(Boolean.TRUE, isDeleted);
    }

}
