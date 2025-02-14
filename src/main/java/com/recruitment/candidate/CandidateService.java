package com.recruitment.candidate;

import com.recruitment.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;

    public Candidate createCandidate(CandidateDto dto) {
        Candidate entity = candidateMapper.toEntity(dto);
        return candidateRepository.save(entity);
    }

    public Candidate updateCandidate(String candidateId, CandidateDto dto) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found: " + candidateId));
        candidate.setFirstName(dto.getFirstName());
        candidate.setLastName(dto.getLastName());
        candidate.setUserId(dto.getUserId());
        return candidateRepository.save(candidate);
    }

    public Optional<Candidate> findById(String candidateId) {
        return candidateRepository.findById(candidateId);
    }

    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    public void deleteCandidate(String candidateId) {
        if (!candidateRepository.existsById(candidateId)) {
            throw new ResourceNotFoundException("Candidate not found: " + candidateId);
        }
        candidateRepository.deleteById(candidateId);
    }
}