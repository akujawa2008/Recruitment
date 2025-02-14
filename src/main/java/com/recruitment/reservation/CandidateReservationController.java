package com.recruitment.reservation;

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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class CandidateReservationController {

    private final CandidateReservationService reservationService;

    @Operation(summary = "Get all reservations")
    @ApiResponse(responseCode = "200", description = "List of reservations returned")
    @GetMapping
    public List<CandidateReservation> getAll() {
        return reservationService.findAll();
    }

    @Operation(summary = "Get reservation by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Reservation found"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")})
    @GetMapping("/{id}")
    public ResponseEntity<CandidateReservation> getById(@PathVariable String id) {
        return reservationService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new reservation")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Reservation created"),
            @ApiResponse(responseCode = "404", description = "Candidate/Slot not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (multiCapacity=false or overlap)"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody CandidateReservationDto reservation) {
        try {
            CandidateReservation created = reservationService.createReservation(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (OverlapException | LimitExceededException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update reservation by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Reservation updated"),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "409", description = "Conflict with existing data"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable String id, @RequestBody CandidateReservationDto updated) {
        try {
            CandidateReservation r = reservationService.updateReservation(id, updated);
            return ResponseEntity.ok(r);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (OverlapException | LimitExceededException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Cancel reservation by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Reservation cancelled"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")})
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String id) {
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.ok("Reservation cancelled.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Delete reservation by ID")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Reservation deleted"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (reservationService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}