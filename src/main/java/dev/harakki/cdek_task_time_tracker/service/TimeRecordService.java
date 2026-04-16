package dev.harakki.cdek_task_time_tracker.service;

import dev.harakki.cdek_task_time_tracker.domain.TimeRecord;
import dev.harakki.cdek_task_time_tracker.dto.EmployeeTimeReportResponse;
import dev.harakki.cdek_task_time_tracker.dto.TimeRecordCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TimeRecordResponse;
import dev.harakki.cdek_task_time_tracker.exception.ResourceNotFoundException;
import dev.harakki.cdek_task_time_tracker.mapper.TimeRecordMapper;
import dev.harakki.cdek_task_time_tracker.mapstruct.mapper.TimeRecordMapstructMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TimeRecordService {

    private final TimeRecordMapper timeRecordMapper;
    private final TimeRecordMapstructMapper mapstructMapper;

    private final TaskService taskService;

    public TimeRecordResponse create(TimeRecordCreateRequest request) {
        if (request.endTime().isBefore(request.startTime())) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }

        // TODO check employeeId is exists (например, отправить запрос на другой сервис)

        // ПРоверка, что сущность задания существует
        taskService.getTaskEntity(request.taskId());

        // Здесь можно добавить проверку на пересечение окон временных записей, НО
        // человек может делать и 2 дела одновременно, верно?
        // Ну, например, обучать стажера И писать код...
        // ...... [###########].........[#####].........
        //...............[#########] <-- можно .........

        var timeRecord = mapstructMapper.toTimeRecord(request);
        timeRecordMapper.insert(timeRecord);
        return mapstructMapper.toTimeRecordResponse(timeRecord);
    }

    public TimeRecordResponse get(@NonNull Long timeRecordId) {
        var timeRecord = getTimeRecordEntity(timeRecordId);
        return mapstructMapper.toTimeRecordResponse(timeRecord);
    }

    public EmployeeTimeReportResponse getEmployeeTimeForTasks(@NonNull Long employeeId, Instant startDate, Instant endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        // Поиск записей, полностью входящих в выбранный диапазон (учитывая записи, частично входящие в него)
        List<TimeRecord> records = timeRecordMapper.findByEmployeeIdAndSoftPeriod(employeeId, startDate, endDate);

        long totalTimeSpentSeconds = 0;

        for (var record : records) {
            var overlapStart = record.getStartTime().isAfter(startDate) ? record.getStartTime() : startDate;
            var overlapEnd = record.getEndTime().isBefore(endDate) ? record.getEndTime() : endDate;

            long seconds = Duration.between(overlapStart, overlapEnd).getSeconds();
            totalTimeSpentSeconds += seconds;
        }

        List<TimeRecordResponse> mappedRecords = records.stream()
                .map(mapstructMapper::toTimeRecordResponse)
                .toList();

        return new EmployeeTimeReportResponse(employeeId, totalTimeSpentSeconds, mappedRecords);
    }

    public TimeRecord getTimeRecordEntity(Long timeRecordId) {
        return timeRecordMapper.findById(timeRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Time record with ID " + timeRecordId + " not found"));
    }

}
