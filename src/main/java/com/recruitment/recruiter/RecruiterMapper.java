package com.recruitment.recruiter;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecruiterMapper {
    Recruiter toEntity(RecruiterDto dto);
}