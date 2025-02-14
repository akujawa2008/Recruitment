package com.recruitment.vacation;

import com.recruitment.exceptions.OverlapException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vacations")
public class VacationController {

    private final VacationService vacationService;

    @Operation(summary = "Get all vacations or by recruiterId")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "List of vacations returned")})
    @GetMapping
    public List<Vacation> getAll(@RequestParam(required = false) String recruiterId) {
        if (recruiterId == null) {
            return vacationService.findAll();
        }
        return vacationService.findByRecruiterId(recruiterId);
    }

    @Operation(summary = "Get vacation by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Vacation found"),
            @ApiResponse(responseCode = "404", description = "Vacation not found")})
    @GetMapping("/{id}")
    public ResponseEntity<Vacation> getById(@PathVariable String id) {
        return vacationService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new vacation")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Vacation created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")})
    @PostMapping
    public ResponseEntity<?> create(@RequestBody VacationDto vacation) {
        try {
            Vacation created = vacationService.createVacation(vacation);
            return ResponseEntity.ok(created);
        } catch (OverlapException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update a vacation")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Vacation updated"),
            @ApiResponse(responseCode = "404", description = "Vacation not found"),
            @ApiResponse(responseCode = "409", description = "Conflict with existing data")})
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody VacationDto dto) {
        try {
            Vacation v = vacationService.updateVacation(id, dto);
            return ResponseEntity.ok(v);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (OverlapException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a vacation by ID")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Vacation deleted"),
            @ApiResponse(responseCode = "404", description = "Vacation not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (vacationService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        vacationService.deleteVacation(id);
        return ResponseEntity.noContent().build();
    }
}