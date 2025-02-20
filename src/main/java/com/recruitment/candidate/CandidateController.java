package com.recruitment.candidate;

import com.recruitment.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
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
@RequestMapping("/api/candidates")

public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @Operation(summary = "Get all candidates")
    @ApiResponse(responseCode = "200", description = "List of candidates returned")
    @GetMapping
    public List<Candidate> getAll() {
        return candidateService.findAll();
    }

    @Operation(summary = "Get candidate by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Candidate found"),
            @ApiResponse(responseCode = "404", description = "Candidate not found")})
    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getById(@PathVariable String id) {
        return candidateService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new candidate")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Candidate created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")})
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CandidateDto candidate) {
        try {
            Candidate created = candidateService.createCandidate(candidate);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update an existing candidate")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Candidate updated"),
            @ApiResponse(responseCode = "404", description = "Candidate not found")})
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody CandidateDto dto) {
        try {
            Candidate c = candidateService.updateCandidate(id, dto);
            return ResponseEntity.ok(c);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a candidate by ID")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Candidate deleted"),
            @ApiResponse(responseCode = "404", description = "Candidate not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (candidateService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }
}