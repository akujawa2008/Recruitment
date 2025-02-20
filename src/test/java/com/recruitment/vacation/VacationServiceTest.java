package com.recruitment.vacation;

import com.recruitment.exceptions.ResourceNotFoundException;
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
class VacationServiceTest {

    @Mock
    private VacationRepository vacationRepository;

    @Mock
    private VacationMapper vacationMapper;

    @InjectMocks
    private VacationService vacationService;

    private Vacation vacation;
    private VacationDto vacationDto;

    @BeforeEach
    void setup() {
        vacation = new Vacation("vac1", "recr1",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3),
                "Some reason", true);

        vacationDto = new VacationDto("vac1", "recr1",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3),
                "Some reason", true);
    }

    @Test
    void createVacation_shouldSave() {
        when(vacationMapper.toEntity(vacationDto)).thenReturn(vacation);
        when(vacationRepository.save(vacation)).thenReturn(vacation);

        Vacation saved = vacationService.createVacation(vacationDto);
        assertNotNull(saved);
        assertEquals("recr1", saved.getRecruiterId());
    }

    @Test
    void updateVacation_whenNotFound_shouldThrow() {
        when(vacationRepository.findById("noid")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> vacationService.updateVacation("noid", vacationDto));
    }

    @Test
    void updateVacation_whenOk_shouldSave() {
        when(vacationRepository.findById("vac1")).thenReturn(Optional.of(vacation));
        when(vacationRepository.save(any(Vacation.class))).thenReturn(vacation);

        Vacation updated = vacationService.updateVacation("vac1", vacationDto);
        assertEquals("recr1", updated.getRecruiterId());
        verify(vacationRepository, times(1)).save(any(Vacation.class));
    }

    @Test
    void findById_shouldReturnVacation() {
        when(vacationRepository.findById("vac1")).thenReturn(Optional.of(vacation));
        Optional<Vacation> opt = vacationService.findById("vac1");
        assertTrue(opt.isPresent());
    }

    @Test
    void findAll_shouldReturnList() {
        when(vacationRepository.findAll()).thenReturn(List.of(vacation));
        List<Vacation> list = vacationService.findAll();
        assertEquals(1, list.size());
    }

    @Test
    void findByRecruiterId_shouldReturnList() {
        when(vacationRepository.findByRecruiterId("recr1")).thenReturn(List.of(vacation));
        List<Vacation> list = vacationService.findByRecruiterId("recr1");
        assertEquals(1, list.size());
    }

    @Test
    void deleteVacation_whenNotFound_shouldThrow() {
        when(vacationRepository.existsById("noid")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> vacationService.deleteVacation("noid"));
    }

    @Test
    void deleteVacation_whenExists_shouldCallDelete() {
        when(vacationRepository.existsById("vac1")).thenReturn(true);
        vacationService.deleteVacation("vac1");
        verify(vacationRepository, times(1)).deleteById("vac1");
    }
}