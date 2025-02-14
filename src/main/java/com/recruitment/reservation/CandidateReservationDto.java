package com.recruitment.reservation;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateReservationDto {
    private String id;
    private String slotId;
    private String candidateId;
    private LocalDateTime reservationTime;
    private String status;
}