package com.recruitment.candidate;

import com.recruitment.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private CandidateMapper candidateMapper;

    @InjectMocks
    private CandidateService candidateService;

    private Candidate candidate;
    private CandidateDto candidateDto;

    @BeforeEach
    void setup() {
        candidate = new Candidate("1", "John", "Doe", "user123");
        candidateDto = new CandidateDto("1", "John", "Doe", "user123");
    }

    @Test
    void createCandidate_shouldSaveAndReturnCandidate() {
        when(candidateMapper.toEntity(candidateDto)).thenReturn(candidate);
        when(candidateRepository.save(candidate)).thenReturn(candidate);

        Candidate result = candidateService.createCandidate(candidateDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(candidateRepository, times(1)).save(candidate);
    }

    @Test
    void updateCandidate_whenExists_shouldUpdateAndReturnCandidate() {
        when(candidateRepository.findById("1")).thenReturn(Optional.of(candidate));

        CandidateDto updateDto = new CandidateDto("1", "Jane", "Smith", "user456");
        Candidate updatedCandidate = new Candidate("1", "Jane", "Smith", "user456");
        when(candidateRepository.save(any(Candidate.class))).thenReturn(updatedCandidate);

        Candidate result = candidateService.updateCandidate("1", updateDto);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
    }

    @Test
    void updateCandidate_whenNotFound_shouldThrowException() {
        when(candidateRepository.findById("99")).thenReturn(Optional.empty());

        CandidateDto dto = new CandidateDto("99", "Jane", "Smith", "user456");

        assertThrows(ResourceNotFoundException.class, () -> candidateService.updateCandidate("99", dto));
    }

    @Test
    void findById_whenExists_shouldReturnCandidate() {
        when(candidateRepository.findById("1")).thenReturn(Optional.of(candidate));
        Optional<Candidate> result = candidateService.findById("1");
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {
        when(candidateRepository.findById("2")).thenReturn(Optional.empty());
        Optional<Candidate> result = candidateService.findById("2");
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_shouldReturnListOfCandidates() {
        when(candidateRepository.findAll()).thenReturn(List.of(candidate));
        List<Candidate> candidates = candidateService.findAll();
        assertEquals(1, candidates.size());
        assertEquals("John", candidates.get(0).getFirstName());
    }

    @Test
    void deleteCandidate_whenExists_shouldDelete() {
        when(candidateRepository.existsById("1")).thenReturn(true);
        candidateService.deleteCandidate("1");
        verify(candidateRepository, times(1)).deleteById("1");
    }

    @Test
    void deleteCandidate_whenNotFound_shouldThrowException() {
        when(candidateRepository.existsById("99")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> candidateService.deleteCandidate("99"));
    }
}