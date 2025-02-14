package com.recruitment.reservation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateReservationRepository extends MongoRepository<CandidateReservation, String> {

    boolean existsBySlotIdAndStatus(String slotId, String status);
}