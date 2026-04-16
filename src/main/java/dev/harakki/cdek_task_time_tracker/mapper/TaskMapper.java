package dev.harakki.cdek_task_time_tracker.mapper;

import dev.harakki.cdek_task_time_tracker.domain.Task;
import dev.harakki.cdek_task_time_tracker.domain.TaskStatus;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface TaskMapper {

    @Insert("INSERT INTO task (name, description, status, created_at, updated_at) VALUES (#{name}, #{description}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Task task);

    @Select("SELECT * FROM task WHERE id = #{id} AND is_deleted = FALSE")
    Optional<Task> findById(Long id);

    @Update("UPDATE task SET name = #{name}, description = #{description}, status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(Task task);

    @Update("UPDATE task SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") TaskStatus status);

    @Update("UPDATE task SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void delete(@Param("id") Long taskId);

}
