package com.recruitment.vacation;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacationDto {
    private String id;
    private String recruiterId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reason;
    private boolean approved;
}