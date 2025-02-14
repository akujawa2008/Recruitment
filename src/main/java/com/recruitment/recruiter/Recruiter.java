package com.recruitment.recruiter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recruiters")
public class Recruiter {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private Integer maxDailyInterviews;
    private Integer maxWeeklyInterviews;
    private Integer maxMonthlyInterviews;
    private String userId;
    private String calendarId;
}
