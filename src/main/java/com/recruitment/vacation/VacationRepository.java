package com.recruitment.vacation;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationRepository extends MongoRepository<Vacation, String> {

    List<Vacation> findByRecruiterId(String recruiterId);

    List<Vacation> findByRecruiterIdAndStartDateLessThanAndEndDateGreaterThan(String recruiterId,
            LocalDateTime endBoundary, LocalDateTime startBoundary);
}