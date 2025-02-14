package com.recruitment.reservation;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "candidate_reservations")
public class CandidateReservation {

    @Id
    private String id;
    private String slotId;
    private String candidateId;
    private LocalDateTime reservationTime;
    private String status;
}