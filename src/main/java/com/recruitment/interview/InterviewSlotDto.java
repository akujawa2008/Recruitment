package com.recruitment.interview;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSlotDto {
    private String id;
    private String recruiterId;
    private String category;
    private Seniority seniority;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean multiCapacity;
    private Integer bufferMinutes;
}