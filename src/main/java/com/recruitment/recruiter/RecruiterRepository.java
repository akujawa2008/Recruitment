package com.recruitment.recruiter;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruiterRepository extends MongoRepository<Recruiter, String> {
}
