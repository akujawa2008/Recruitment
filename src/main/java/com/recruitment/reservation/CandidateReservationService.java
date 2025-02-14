package com.recruitment.reservation;

import com.recruitment.candidate.CandidateRepository;
import com.recruitment.exceptions.OverlapException;
import com.recruitment.exceptions.ResourceNotFoundException;
import com.recruitment.interview.InterviewSlot;
import com.recruitment.interview.InterviewSlotRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidateReservationService {

    private final CandidateReservationRepository reservationRepository;
    private final CandidateRepository candidateRepository;
    private final InterviewSlotRepository slotRepository;
    private final CandidateReservationMapper reservationMapper;

    public CandidateReservation createReservation(CandidateReservationDto dto) {
        InterviewSlot slot = slotRepository.findById(dto.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found: " + dto.getSlotId()));
        if (!candidateRepository.existsById(dto.getCandidateId())) {
            throw new ResourceNotFoundException("Candidate not found: " + dto.getCandidateId());
        }
        if (!slot.isMultiCapacity()) {
            boolean alreadyReserved = reservationRepository.existsBySlotIdAndStatus(slot.getId(), "ACTIVE");
            if (alreadyReserved) {
                throw new OverlapException("Slot is already reserved (multiCapacity=false).");
            }
        }
        CandidateReservation entity = reservationMapper.toEntity(dto);
        entity.setReservationTime(LocalDateTime.now());
        entity.setStatus("ACTIVE");
        return reservationRepository.save(entity);
    }

    public CandidateReservation updateReservation(String reservationId, CandidateReservationDto dto) {
        CandidateReservation existing = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));
        existing.setCandidateId(dto.getCandidateId());
        existing.setSlotId(dto.getSlotId());
        existing.setStatus(dto.getStatus());
        existing.setReservationTime(dto.getReservationTime());
        return reservationRepository.save(existing);
    }

    public void cancelReservation(String reservationId) {
        CandidateReservation existing = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));
        existing.setStatus("CANCELLED");
        reservationRepository.save(existing);
    }

    public Optional<CandidateReservation> findById(String reservationId) {
        return reservationRepository.findById(reservationId);
    }

    public List<CandidateReservation> findAll() {
        return reservationRepository.findAll();
    }

    public void deleteReservation(String reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new ResourceNotFoundException("Reservation not found: " + reservationId);
        }
        reservationRepository.deleteById(reservationId);
    }
}