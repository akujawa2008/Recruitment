package com.recruitment.recruiter;

import com.recruitment.exceptions.LimitExceededException;
import com.recruitment.exceptions.OverlapException;
import com.recruitment.exceptions.ResourceNotFoundException;
import com.recruitment.interview.InterviewSlot;
import com.recruitment.interview.InterviewSlotRepository;
import com.recruitment.vacation.Vacation;
import com.recruitment.vacation.VacationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final InterviewSlotRepository interviewSlotRepository;
    private final VacationRepository vacationRepository;
    private final RecruiterMapper recruiterMapper;

    public Recruiter createRecruiter(RecruiterDto dto) {
        Recruiter entity = recruiterMapper.toEntity(dto);
        return recruiterRepository.save(entity);
    }

    public Recruiter updateRecruiter(String id, RecruiterDto dto) {
        Recruiter existing = recruiterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found: " + id));
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setMaxDailyInterviews(dto.getMaxDailyInterviews());
        existing.setMaxWeeklyInterviews(dto.getMaxWeeklyInterviews());
        existing.setMaxMonthlyInterviews(dto.getMaxMonthlyInterviews());
        return recruiterRepository.save(existing);
    }

    public Optional<Recruiter> findById(String id) {
        return recruiterRepository.findById(id);
    }

    public List<Recruiter> findAll() {
        return recruiterRepository.findAll();
    }

    public void deleteById(String id) {
        if (!recruiterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recruiter not found: " + id);
        }
        recruiterRepository.deleteById(id);
    }

    public void validateNewSlot(Recruiter recruiter, InterviewSlot slot) {
        if (recruiter == null || slot == null) {
            throw new IllegalArgumentException("Recruiter or slot cannot be null.");
        }
        validateLimits(recruiter, slot);
        validateVacationOverlap(recruiter, slot);
        validateSlotOverlaps(recruiter, slot);
    }

    private void validateLimits(Recruiter recruiter, InterviewSlot slot) {
        if (recruiter.getMaxDailyInterviews() != null && recruiter.getMaxDailyInterviews() > 0) {
            long dailyCount = countSlotsInSameDay(recruiter, slot.getStartTime());
            if (dailyCount >= recruiter.getMaxDailyInterviews()) {
                throw new LimitExceededException("Daily limit reached for recruiter: " + recruiter.getId());
            }
        }
        if (recruiter.getMaxWeeklyInterviews() != null && recruiter.getMaxWeeklyInterviews() > 0) {
            long weeklyCount = countSlotsInSameWeek(recruiter, slot.getStartTime());
            if (weeklyCount >= recruiter.getMaxWeeklyInterviews()) {
                throw new LimitExceededException("Weekly limit reached for recruiter: " + recruiter.getId());
            }
        }
        if (recruiter.getMaxMonthlyInterviews() != null && recruiter.getMaxMonthlyInterviews() > 0) {
            long monthlyCount = countSlotsInSameMonth(recruiter, slot.getStartTime());
            if (monthlyCount >= recruiter.getMaxMonthlyInterviews()) {
                throw new LimitExceededException("Monthly limit reached for recruiter: " + recruiter.getId());
            }
        }
    }

    private void validateVacationOverlap(Recruiter recruiter, InterviewSlot slot) {
        List<Vacation> overlapping = vacationRepository.findByRecruiterIdAndStartDateLessThanAndEndDateGreaterThan(
                recruiter.getId(), slot.getEndTime(), slot.getStartTime());
        if (!overlapping.isEmpty()) {
            throw new OverlapException("Recruiter is on vacation during this slot.");
        }
    }

    private void validateSlotOverlaps(Recruiter recruiter, InterviewSlot slot) {
        int buffer = (slot.getBufferMinutes() != null) ? slot.getBufferMinutes() : 0;
        LocalDateTime startBuf = slot.getStartTime().minusMinutes(buffer);
        LocalDateTime endBuf = slot.getEndTime().plusMinutes(buffer);

        List<InterviewSlot> overlap = interviewSlotRepository.findByRecruiterIdAndStartTimeLessThanAndEndTimeGreaterThan(
                recruiter.getId(), endBuf, startBuf);
        for (InterviewSlot existing : overlap) {
            if (!existing.isMultiCapacity() || !slot.isMultiCapacity()) {
                throw new OverlapException("Slot conflict with ID " + existing.getId());
            }
        }
    }

    private long countSlotsInSameDay(Recruiter recruiter, LocalDateTime dateTime) {
        LocalDate day = dateTime.toLocalDate();
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.atTime(23, 59, 59);
        return interviewSlotRepository.countByRecruiterIdAndStartTimeBetween(recruiter.getId(), startOfDay, endOfDay);
    }

    private long countSlotsInSameWeek(Recruiter recruiter, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalDate monday = date.with(java.time.DayOfWeek.MONDAY);
        LocalDate sunday = date.with(java.time.DayOfWeek.SUNDAY);
        LocalDateTime start = monday.atStartOfDay();
        LocalDateTime end = sunday.atTime(23, 59, 59);
        return interviewSlotRepository.countByRecruiterIdAndStartTimeBetween(recruiter.getId(), start, end);
    }

    private long countSlotsInSameMonth(Recruiter recruiter, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime start = firstDay.atStartOfDay();
        LocalDateTime end = lastDay.atTime(23, 59, 59);
        return interviewSlotRepository.countByRecruiterIdAndStartTimeBetween(recruiter.getId(), start, end);
    }
}