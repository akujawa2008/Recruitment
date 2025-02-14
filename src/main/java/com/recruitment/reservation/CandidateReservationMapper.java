package com.recruitment.reservation;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CandidateReservationMapper {
    CandidateReservation toEntity(CandidateReservationDto dto);
}