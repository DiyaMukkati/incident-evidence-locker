package com.internship.tool.controller;

import com.internship.tool.entity.Incident;
import com.internship.tool.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Incident REST Controller
 * Base URL: /api/incidents
 * Day 4 — Java Developer 1
 */
@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
@Tag(name = "Incidents", description = "Incident Evidence Locker API")
@CrossOrigin(origins = "http://localhost:5173") // Allow React frontend
public class IncidentController {

    private final IncidentService incidentService;

    // ── GET /incidents/all?page=0&size=10 ─────────────────────────
    @GetMapping("/all")
    @Operation(summary = "Get all incidents paginated")
    public ResponseEntity<Page<Incident>> getAllIncidents(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(incidentService.getAllIncidents(pageable));
    }

    // ── GET /incidents/{id} ───────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Get one incident by ID")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
        // throws 404 automatically if not found (handled by exception)
    }

    // ── POST /incidents/create ────────────────────────────────────
    @PostMapping("/create")
    @Operation(summary = "Create a new incident")
    public ResponseEntity<Incident> createIncident(
            @Valid @RequestBody Incident incident) {
        Incident created = incidentService.createIncident(incident);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── PUT /incidents/{id} ───────────────────────────────────────
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing incident")
    public ResponseEntity<Incident> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody Incident incident) {
        return ResponseEntity.ok(incidentService.updateIncident(id, incident));
    }

    // ── DELETE /incidents/{id} (soft delete) ──────────────────────
    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete an incident")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // ── GET /incidents/search?q=keyword ───────────────────────────
    @GetMapping("/search")
    @Operation(summary = "Search incidents by keyword")
    public ResponseEntity<Page<Incident>> searchIncidents(
            @RequestParam String q,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(incidentService.searchIncidents(q, pageable));
    }

    // ── GET /incidents/stats ──────────────────────────────────────
    @GetMapping("/stats")
    @Operation(summary = "Get dashboard KPI stats")
    public ResponseEntity<IncidentService.DashboardStats> getStats() {
        return ResponseEntity.ok(incidentService.getStats());
    }

    // ── GET /incidents/status/{status} ────────────────────────────
    @GetMapping("/status/{status}")
    @Operation(summary = "Filter incidents by status")
    public ResponseEntity<Page<Incident>> getByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                incidentService.getIncidentsByStatus(status, pageable));
    }
}
