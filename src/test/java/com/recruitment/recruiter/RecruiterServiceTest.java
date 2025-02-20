package com.recruitment.recruiter;

import com.recruitment.exceptions.OverlapException;
import com.recruitment.exceptions.ResourceNotFoundException;
import com.recruitment.interview.InterviewSlot;
import com.recruitment.interview.InterviewSlotRepository;
import com.recruitment.vacation.Vacation;
import com.recruitment.vacation.VacationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruiterServiceTest {

    @Mock
    private RecruiterRepository recruiterRepository;

    @Mock
    private InterviewSlotRepository interviewSlotRepository;

    @Mock
    private VacationRepository vacationRepository;

    @Mock
    private RecruiterMapper recruiterMapper;

    @InjectMocks
    private RecruiterService recruiterService;

    private Recruiter recruiter;
    private RecruiterDto recruiterDto;

    @BeforeEach
    void setup() {
        recruiter = new Recruiter();
        recruiter.setId("recr1");
        recruiter.setFirstName("John");
        recruiter.setLastName("Doe");
        recruiter.setMaxDailyInterviews(2);

        recruiterDto = new RecruiterDto("recr1", "John", "Doe", 2, 5, 10);
    }

    @Test
    void createRecruiter_shouldSave() {
        when(recruiterMapper.toEntity(recruiterDto)).thenReturn(recruiter);
        when(recruiterRepository.save(recruiter)).thenReturn(recruiter);

        Recruiter saved = recruiterService.createRecruiter(recruiterDto);
        assertNotNull(saved);
        assertEquals("John", saved.getFirstName());
    }

    @Test
    void updateRecruiter_whenNotFound_shouldThrow() {
        when(recruiterRepository.findById("noid")).thenReturn(Optional.empty());
        RecruiterDto dto = new RecruiterDto("noid", "Jane", "Smith", 3, 6, 12);
        assertThrows(ResourceNotFoundException.class, () -> recruiterService.updateRecruiter("noid", dto));
    }

    @Test
    void findById_shouldReturn() {
        when(recruiterRepository.findById("recr1")).thenReturn(Optional.of(recruiter));
        Optional<Recruiter> opt = recruiterService.findById("recr1");
        assertTrue(opt.isPresent());
    }

    @Test
    void findAll_shouldReturnList() {
        when(recruiterRepository.findAll()).thenReturn(List.of(recruiter));
        List<Recruiter> list = recruiterService.findAll();
        assertEquals(1, list.size());
    }

    @Test
    void deleteById_whenNotFound_shouldThrow() {
        when(recruiterRepository.existsById("x")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> recruiterService.deleteById("x"));
    }

    @Test
    void deleteById_whenFound_shouldDelete() {
        when(recruiterRepository.existsById("recr1")).thenReturn(true);
        recruiterService.deleteById("recr1");
        verify(recruiterRepository, times(1)).deleteById("recr1");
    }

    @Test
    void validateNewSlot_whenOnVacation_shouldThrowOverlap() {
        InterviewSlot slot = new InterviewSlot();
        slot.setStartTime(LocalDateTime.now());
        slot.setEndTime(LocalDateTime.now().plusHours(1));

        when(vacationRepository.findByRecruiterIdAndStartDateLessThanAndEndDateGreaterThan(
                "recr1", slot.getEndTime(), slot.getStartTime()))
                .thenReturn(List.of(new Vacation()));

        assertThrows(OverlapException.class, () -> recruiterService.validateNewSlot(recruiter, slot));
    }
}
