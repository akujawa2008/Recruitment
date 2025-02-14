package com.recruitment.candidate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDto {
    private String id;
    private String firstName;
    private String lastName;
    private String userId;
}