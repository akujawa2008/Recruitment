package com.recruitment.candidate;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateMapper {
    Candidate toEntity(CandidateDto dto);
}