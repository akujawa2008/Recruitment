package com.recruitment.recruiter;

import com.recruitment.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/recruiters")
public class RecruiterController {

    private final RecruiterService recruiterService;

    @Operation(summary = "Get all recruiters")
    @ApiResponse(responseCode = "200", description = "List of recruiters returned")
    @GetMapping
    public List<Recruiter> getAll() {
        return recruiterService.findAll();
    }

    @Operation(summary = "Get recruiter by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Recruiter found"),
            @ApiResponse(responseCode = "404", description = "Recruiter not found")})
    @GetMapping("/{id}")
    public ResponseEntity<Recruiter> getById(@PathVariable String id) {
        return recruiterService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new recruiter")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Recruiter created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")})
    @PostMapping
    public ResponseEntity<?> create(@RequestBody RecruiterDto recruiter) {
        try {
            Recruiter saved = recruiterService.createRecruiter(recruiter);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update existing recruiter by ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Recruiter updated"),
            @ApiResponse(responseCode = "404", description = "Recruiter not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody RecruiterDto data) {
        try {
            Recruiter updated = recruiterService.updateRecruiter(id, data);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Delete recruiter by ID")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Recruiter deleted"),
            @ApiResponse(responseCode = "404", description = "Recruiter not found")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (recruiterService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        recruiterService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}