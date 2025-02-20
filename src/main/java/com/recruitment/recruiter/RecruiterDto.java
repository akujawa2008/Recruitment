package com.recruitment.recruiter;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RecruiterDto {
    private String id;
    private String firstName;
    private String lastName;
    private Integer maxDailyInterviews;
    private Integer maxWeeklyInterviews;
    private Integer maxMonthlyInterviews;
    private String calendarId;
    private String userId;

    public RecruiterDto(String recr1, String john, String doe, int i, int i1, int i2) {
    }
}