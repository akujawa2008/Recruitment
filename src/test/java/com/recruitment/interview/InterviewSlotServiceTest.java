package com.recruitment.interview;

import com.recruitment.exceptions.ResourceNotFoundException;
import com.recruitment.google.calendar.GoogleCalendarService;
import com.recruitment.recruiter.Recruiter;
import com.recruitment.recruiter.RecruiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewSlotServiceTest {

    @Mock
    private InterviewSlotRepository interviewSlotRepository;

    @Mock
    private RecruiterService recruiterService;

    @Mock
    private InterviewSlotMapper slotMapper;

    @Mock
    private GoogleCalendarService googleCalendarService;

    @InjectMocks
    private InterviewSlotService slotService;

    private Recruiter recruiter;
    private InterviewSlot entity;
    private InterviewSlotDto dto;

    @BeforeEach
    void setup() {
        recruiter = new Recruiter();
        recruiter.setId("recr1");
        recruiter.setCalendarId("cal123");

        entity = new InterviewSlot();
        entity.setId("slot1");
        dto = new InterviewSlotDto();
        dto.setCategory("JAVA");
        dto.setSeniority(Seniority.valueOf("MID"));
    }

    @Test
    void createSlot_whenRecruiterNotFound_shouldThrow() {
        when(recruiterService.findById("recrX")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> slotService.createSlot(dto, "recrX"));
    }

    @Test
    void createSlot_whenOk_shouldSaveSlotAndCreateEvent() throws Exception {
        when(recruiterService.findById("recr1")).thenReturn(Optional.of(recruiter));
        when(slotMapper.toEntity(dto)).thenReturn(entity);
        when(interviewSlotRepository.save(entity)).thenReturn(entity);
        when(googleCalendarService.createCalendarEvent("cal123", entity)).thenReturn("event123");

        InterviewSlot result = slotService.createSlot(dto, "recr1");
        assertNotNull(result);
        assertEquals("slot1", result.getId());
        verify(interviewSlotRepository, times(2)).save(entity);
    }

    @Test
    void updateSlot_whenNotFound_shouldThrow() {
        when(interviewSlotRepository.findById("slotX")).thenReturn(Optional.empty());
        InterviewSlotDto d = new InterviewSlotDto();
        assertThrows(ResourceNotFoundException.class, () -> slotService.updateSlot("slotX", d));
    }

    @Test
    void deleteSlot_whenNotFound_shouldThrow() {
        when(interviewSlotRepository.findById("noid")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> slotService.deleteSlot("noid"));
    }

    @Test
    void findById_shouldReturnSlot() {
        when(interviewSlotRepository.findById("slot1")).thenReturn(Optional.of(entity));
        Optional<InterviewSlot> opt = slotService.findById("slot1");
        assertTrue(opt.isPresent());
    }

    @Test
    void findAll_shouldReturnSlotsList() {
        when(interviewSlotRepository.findAll()).thenReturn(List.of(entity));
        List<InterviewSlot> list = slotService.findAll();
        assertEquals(1, list.size());
    }

    @Test
    void getSlotsByCategoryAndSeniority_shouldReturnFiltered() {
        when(interviewSlotRepository.findByCategoryAndSeniority("JAVA", "MID"))
                .thenReturn(List.of(entity));
        List<InterviewSlot> list = slotService.getSlotsByCategoryAndSeniority("JAVA", "MID");
        assertEquals(1, list.size());
    }
}