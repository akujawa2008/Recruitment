package com.recruitment.interview;

import com.recruitment.exceptions.LimitExceededException;
import com.recruitment.exceptions.OverlapException;
import com.recruitment.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/interview-slots")
public class InterviewSlotController {

    private final InterviewSlotService slotService;

    @Operation(summary = "Get all slots")
    @ApiResponse(responseCode = "200", description = "List of slots returned")
    @GetMapping
    public List<InterviewSlot> getAll() {
        return slotService.findAll();
    }

    @Operation(summary = "Get slot by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Slot found"),
            @ApiResponse(responseCode = "404", description = "Slot not found")})
    @GetMapping("/{id}")
    public ResponseEntity<InterviewSlot> getById(@PathVariable String id) {
        return slotService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new slot for given recruiterId")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Slot created"),
            @ApiResponse(responseCode = "404", description = "Recruiter not found"),
            @ApiResponse(responseCode = "409", description = "Overlap or limit exceeded"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping
    public ResponseEntity<?> createSlot(@RequestParam String recruiterId, @RequestBody InterviewSlotDto slot) {
        try {
            InterviewSlot created = slotService.createSlot(slot, recruiterId);
            return ResponseEntity.ok(created);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (OverlapException | LimitExceededException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update an existing slot by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Slot updated"),
            @ApiResponse(responseCode = "404", description = "Slot not found"),
            @ApiResponse(responseCode = "409", description = "Overlap or limit exceeded"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSlot(@PathVariable String id, @RequestBody InterviewSlotDto dto) {
        try {
            InterviewSlot result = slotService.updateSlot(id, dto);
            return ResponseEntity.ok(result);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (OverlapException | LimitExceededException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Search slots by category and seniority")
    @ApiResponse(responseCode = "200", description = "List of filtered slots returned")
    @GetMapping("/search")
    public List<InterviewSlot> getSlotsByCategoryAndSeniority(@RequestParam String category,
            @RequestParam String seniority) {
        return slotService.getSlotsByCategoryAndSeniority(category, seniority);
    }

    @Operation(summary = "Delete slot by ID")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Slot deleted"),
            @ApiResponse(responseCode = "404", description = "Slot not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable String id) {
        if (slotService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        slotService.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }
}
