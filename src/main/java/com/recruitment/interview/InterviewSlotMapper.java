package com.recruitment.interview;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InterviewSlotMapper {
    InterviewSlot toEntity(InterviewSlotDto dto);
}