package com.recruitment.vacation;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VacationMapper {
    Vacation toEntity(VacationDto dto);
}