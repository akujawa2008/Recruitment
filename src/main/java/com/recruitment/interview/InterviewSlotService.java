package com.recruitment.interview;

import com.recruitment.exceptions.ResourceNotFoundException;
import com.recruitment.google.calendar.GoogleCalendarService;
import com.recruitment.recruiter.Recruiter;
import com.recruitment.recruiter.RecruiterService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class InterviewSlotService {

    private static final Logger log = LoggerFactory.getLogger(InterviewSlotService.class);

    private final InterviewSlotRepository interviewSlotRepository;
    private final RecruiterService recruiterService;
    private final InterviewSlotMapper slotMapper;
    private final GoogleCalendarService googleCalendarService;

    public InterviewSlot createSlot(InterviewSlotDto dto, String recruiterId) {
        Recruiter recruiter = recruiterService.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found: " + recruiterId));

        InterviewSlot entity = slotMapper.toEntity(dto);
        entity.setRecruiterId(recruiterId);
        recruiterService.validateNewSlot(recruiter, entity);

        InterviewSlot saved = interviewSlotRepository.save(entity);

        if (recruiter.getCalendarId() != null) {
            try {
                String eventId = googleCalendarService.createCalendarEvent(recruiter.getCalendarId(), saved);
                saved.setGoogleCalendarEventId(eventId);
                saved = interviewSlotRepository.save(saved);
            } catch (Exception e) {
                log.error("Failed to create event in Google Calendar for recruiterId {}: {}", recruiterId,
                        e.getMessage(), e);
            }
        }
        return saved;
    }

    public InterviewSlot updateSlot(String slotId, InterviewSlotDto dto) {
        InterviewSlot slot = interviewSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found: " + slotId));
        boolean timeChanged =
                !slot.getStartTime().equals(dto.getStartTime()) || !slot.getEndTime().equals(dto.getEndTime());

        slot.setCategory(dto.getCategory());
        slot.setSeniority(dto.getSeniority());
        slot.setStartTime(dto.getStartTime());
        slot.setEndTime(dto.getEndTime());
        slot.setMultiCapacity(dto.isMultiCapacity());
        slot.setBufferMinutes(dto.getBufferMinutes());

        if (timeChanged) {
            Recruiter recruiter = recruiterService.findById(slot.getRecruiterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found for slot: " + slotId));
            recruiterService.validateNewSlot(recruiter, slot);
        }

        InterviewSlot saved = interviewSlotRepository.save(slot);

        Recruiter recruiter = recruiterService.findById(slot.getRecruiterId()).orElse(null);
        if (recruiter != null && recruiter.getCalendarId() != null) {
            try {
                googleCalendarService.updateCalendarEvent(recruiter.getCalendarId(), saved);
            } catch (Exception e) {
                log.error("Failed to update event in Google Calendar for slotId {}: {}", slotId, e.getMessage(), e);
            }
        }
        return saved;
    }

    public void deleteSlot(String slotId) {
        InterviewSlot slot = interviewSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found: " + slotId));

        Recruiter recruiter = recruiterService.findById(slot.getRecruiterId()).orElse(null);
        if (recruiter != null && recruiter.getCalendarId() != null) {
            try {
                googleCalendarService.deleteCalendarEvent(recruiter.getCalendarId(), slot.getGoogleCalendarEventId());
            } catch (Exception e) {
                log.error("Failed to delete event in Google Calendar for slotId {}: {}", slotId, e.getMessage(), e);
            }
        }
        interviewSlotRepository.deleteById(slotId);
    }

    public Optional<InterviewSlot> findById(String slotId) {
        return interviewSlotRepository.findById(slotId);
    }

    public List<InterviewSlot> findAll() {
        return interviewSlotRepository.findAll();
    }

    public List<InterviewSlot> getSlotsByCategoryAndSeniority(String category, String seniority) {
        return interviewSlotRepository.findByCategoryAndSeniority(category, seniority);
    }
}