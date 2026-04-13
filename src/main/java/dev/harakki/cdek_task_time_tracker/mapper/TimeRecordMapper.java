package dev.harakki.cdek_task_time_tracker.mapper;

import dev.harakki.cdek_task_time_tracker.domain.TimeRecord;
import org.apache.ibatis.annotations.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface TimeRecordMapper {

    @Insert("INSERT INTO time_record (employee_id, task_id, start_time, end_time, work_description, created_at) VALUES (#{employeeId}, #{taskId}, #{startTime}, #{endTime}, #{workDescription}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(TimeRecord timeRecord);

    @Select("SELECT * FROM time_record WHERE id = #{id}")
    Optional<TimeRecord> findById(Long id);

    @Select("""
        SELECT * FROM time_record
        WHERE employee_id = #{employeeId}
          AND start_time >= #{from}
          AND end_time <= #{to}
    """)
    List<TimeRecord> findByEmployeeIdAndPeriod(
            @Param("employeeId") Long employeeId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

}
