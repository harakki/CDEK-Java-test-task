package dev.harakki.cdek_task_time_tracker.mapstruct.mapper;

import dev.harakki.cdek_task_time_tracker.domain.Task;
import dev.harakki.cdek_task_time_tracker.dto.TaskCreateRequest;
import dev.harakki.cdek_task_time_tracker.dto.TaskPatchRequest;
import dev.harakki.cdek_task_time_tracker.dto.TaskResponse;
import dev.harakki.cdek_task_time_tracker.dto.TaskUpdateStatusRequest;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TaskMapstructMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Task toTask(TaskCreateRequest taskCreateRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(TaskPatchRequest dto, @MappingTarget Task task);

    TaskResponse toTaskResponse(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(TaskUpdateStatusRequest dto, @MappingTarget Task task);

}
