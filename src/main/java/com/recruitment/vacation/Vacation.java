package com.recruitment.vacation;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "vacations")
public class Vacation {

    @Id
    private String id;
    private String recruiterId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reason;
    private boolean approved;
}