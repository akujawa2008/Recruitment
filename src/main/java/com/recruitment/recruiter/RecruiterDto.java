package com.recruitment.recruiter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterDto {
    private String id;
    private String firstName;
    private String lastName;
    private Integer maxDailyInterviews;
    private Integer maxWeeklyInterviews;
    private Integer maxMonthlyInterviews;
}