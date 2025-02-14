package com.recruitment.vacation;

import com.recruitment.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;
    private final VacationMapper vacationMapper;

    public Vacation createVacation(VacationDto dto) {
        Vacation entity = vacationMapper.toEntity(dto);
        return vacationRepository.save(entity);
    }

    public Vacation updateVacation(String vacationId, VacationDto dto) {
        Vacation existing = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new ResourceNotFoundException("Vacation not found: " + vacationId));
        existing.setRecruiterId(dto.getRecruiterId());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setReason(dto.getReason());
        existing.setApproved(dto.isApproved());
        return vacationRepository.save(existing);
    }

    public Optional<Vacation> findById(String vacationId) {
        return vacationRepository.findById(vacationId);
    }

    public List<Vacation> findAll() {
        return vacationRepository.findAll();
    }

    public List<Vacation> findByRecruiterId(String recruiterId) {
        return vacationRepository.findByRecruiterId(recruiterId);
    }

    public void deleteVacation(String vacationId) {
        if (!vacationRepository.existsById(vacationId)) {
            throw new ResourceNotFoundException("Vacation not found: " + vacationId);
        }
        vacationRepository.deleteById(vacationId);
    }
}