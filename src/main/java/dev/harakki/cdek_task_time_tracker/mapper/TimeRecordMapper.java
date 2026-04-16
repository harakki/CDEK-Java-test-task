package dev.harakki.cdek_task_time_tracker.mapper;

import dev.harakki.cdek_task_time_tracker.domain.TimeRecord;
import org.apache.ibatis.annotations.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Mapper
public interface TimeRecordMapper {

    @Insert("INSERT INTO time_record (employee_id, task_id, start_time, end_time, work_description, created_at, updated_at) VALUES (#{employeeId}, #{taskId}, #{startTime}, #{endTime}, #{workDescription}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(TimeRecord timeRecord);

    @Select("SELECT * FROM time_record WHERE id = #{id} AND is_deleted = FALSE")
    Optional<TimeRecord> findById(Long id);

    @Select("""
                SELECT * FROM time_record
                WHERE employee_id = #{employeeId}
                  AND start_time >= #{from}
                  AND end_time <= #{to}
                  AND is_deleted = FALSE
            """)
    List<TimeRecord> findByEmployeeIdAndPeriod(
            @Param("employeeId") Long employeeId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    @Select("""
                SELECT * FROM time_record
                WHERE employee_id = #{employeeId}
                  AND start_time < #{to}
                  AND end_time > #{from}
                  AND is_deleted = FALSE
            """)
    List<TimeRecord> findByEmployeeIdAndSoftPeriod(
            @Param("employeeId") Long employeeId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    @Update("UPDATE time_record SET employee_id = #{employeeId}, task_id = #{taskId}, start_time = #{startTime}, end_time = #{endTime}, work_description = #{workDescription}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(TimeRecord timeRecord);

    @Update("UPDATE time_record SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void delete(@Param("id") Long timeRecordId);

}
