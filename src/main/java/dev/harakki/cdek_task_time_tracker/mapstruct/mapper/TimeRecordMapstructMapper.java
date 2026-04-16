package dev.harakki.cdek_task_time_tracker.mapstruct.mapper;

import dev.harakki.cdek_task_time_tracker.domain.TimeRecord;
import dev.harakki.cdek_task_time_tracker.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TimeRecordMapstructMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    TimeRecord toTimeRecord(TimeRecordCreateRequest timeRecordCreateRequest);

    TimeRecordResponse toTimeRecordResponse(TimeRecord timeRecord);

}
