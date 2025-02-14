package com.recruitment.interview;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InterviewSlotRepository extends MongoRepository<InterviewSlot, String> {

    long countByRecruiterIdAndStartTimeBetween(String recruiterId, LocalDateTime start, LocalDateTime end);

    // Wyszukiwanie nakładających się terminów (overlap):
    List<InterviewSlot> findByRecruiterIdAndStartTimeLessThanAndEndTimeGreaterThan(
            String recruiterId,
            LocalDateTime endBoundary,
            LocalDateTime startBoundary);

    List<InterviewSlot> findByCategoryAndSeniority(String category, String seniority);

}
