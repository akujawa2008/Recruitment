package com.recruitment.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "interview_slots")
public class InterviewSlot {

    @Id
    private String id;
    private String category;
    private Seniority seniority;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean multiCapacity;
    private Integer bufferMinutes;
    private String recruiterId;
    private String googleCalendarEventId;
}
