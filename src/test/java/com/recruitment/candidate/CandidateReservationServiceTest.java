package com.recruitment.candidate;

import com.recruitment.exceptions.OverlapException;
import com.recruitment.exceptions.ResourceNotFoundException;
import com.recruitment.interview.InterviewSlot;
import com.recruitment.interview.InterviewSlotRepository;
import com.recruitment.reservation.*;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateReservationServiceTest {

    @Mock
    private CandidateReservationRepository reservationRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private InterviewSlotRepository slotRepository;

    @Mock
    private CandidateReservationMapper reservationMapper;

    @InjectMocks
    private CandidateReservationService reservationService;

    private CandidateReservation reservation;
    private CandidateReservationDto reservationDto;
    private InterviewSlot slot;

    @BeforeEach
    void setup() {
        slot = new InterviewSlot();
        slot.setId("slot123");
        slot.setMultiCapacity(false);

        reservation = new CandidateReservation("r1", "slot123", "cand123", LocalDateTime.now(), "ACTIVE");
        reservationDto = new CandidateReservationDto("r1", "slot123", "cand123", LocalDateTime.now(), "ACTIVE");
    }

    @Test
    void createReservation_whenSlotNotFound_shouldThrow() {
        when(slotRepository.findById("slot123")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reservationService.createReservation(reservationDto));
    }

    @Test
    void createReservation_whenCandidateNotFound_shouldThrow() {
        when(slotRepository.findById("slot123")).thenReturn(Optional.of(slot));
        when(candidateRepository.existsById("cand123")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> reservationService.createReservation(reservationDto));
    }

    @Test
    void createReservation_whenAlreadyReservedAndMultiCapacityFalse_shouldThrowOverlap() {
        when(slotRepository.findById("slot123")).thenReturn(Optional.of(slot));
        when(candidateRepository.existsById("cand123")).thenReturn(true);
        when(reservationRepository.existsBySlotIdAndStatus("slot123", "ACTIVE")).thenReturn(true);
        assertThrows(OverlapException.class, () -> reservationService.createReservation(reservationDto));
    }

    @Test
    void updateReservation_whenNotFound_shouldThrow() {
        when(reservationRepository.findById("nope")).thenReturn(Optional.empty());
        CandidateReservationDto dto = new CandidateReservationDto("nope", "slotX", "candX", null, "ACTIVE");
        assertThrows(ResourceNotFoundException.class, () -> reservationService.updateReservation("nope", dto));
    }

    @Test
    void updateReservation_whenOk_shouldSave() {
        when(reservationRepository.findById("r1")).thenReturn(Optional.of(reservation));
        CandidateReservationDto dto = new CandidateReservationDto("r1", "slotXYZ", "candXYZ", LocalDateTime.now(), "ACTIVE");
        CandidateReservation updated = new CandidateReservation("r1", "slotXYZ", "candXYZ", LocalDateTime.now(), "ACTIVE");
        when(reservationRepository.save(any(CandidateReservation.class))).thenReturn(updated);

        CandidateReservation result = reservationService.updateReservation("r1", dto);
        assertEquals("slotXYZ", result.getSlotId());
        verify(reservationRepository, times(1)).save(any(CandidateReservation.class));
    }

    @Test
    void cancelReservation_whenNotFound_shouldThrow() {
        when(reservationRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reservationService.cancelReservation("x"));
    }

    @Test
    void cancelReservation_shouldSetStatusCancelled() {
        when(reservationRepository.findById("r1")).thenReturn(Optional.of(reservation));
        reservationService.cancelReservation("r1");
        assertEquals("CANCELLED", reservation.getStatus());
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void findById_shouldReturnReservation() {
        when(reservationRepository.findById("r1")).thenReturn(Optional.of(reservation));
        Optional<CandidateReservation> opt = reservationService.findById("r1");
        assertTrue(opt.isPresent());
        assertEquals("cand123", opt.get().getCandidateId());
    }

    @Test
    void findAll_shouldReturnAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));
        List<CandidateReservation> list = reservationService.findAll();
        assertEquals(1, list.size());
    }

    @Test
    void deleteReservation_whenNotFound_shouldThrow() {
        when(reservationRepository.existsById("noid")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> reservationService.deleteReservation("noid"));
    }

    @Test
    void deleteReservation_whenExists_shouldCallDelete() {
        when(reservationRepository.existsById("r1")).thenReturn(true);
        reservationService.deleteReservation("r1");
        verify(reservationRepository, times(1)).deleteById("r1");
    }
}
