package dev.harakki.cdek_task_time_tracker.mapper;

import dev.harakki.cdek_task_time_tracker.domain.Task;
import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface TaskMapper {

    @Insert("INSERT INTO task (name, description, status, created_at, updated_at) VALUES (#{name}, #{description}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Task task);

    @Select("SELECT * FROM task WHERE id = #{id}")
    Optional<Task> findById(Long id);

    @Update("UPDATE task SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") TaskStatus status);

}
