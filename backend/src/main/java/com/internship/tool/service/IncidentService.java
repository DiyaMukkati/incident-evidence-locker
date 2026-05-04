package com.internship.tool.service;

import com.internship.tool.entity.Incident;
import com.internship.tool.exception.Exceptions.BadRequestException;
import com.internship.tool.exception.Exceptions.ResourceNotFoundException;
import com.internship.tool.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Incident Service — business logic layer.
 * Controllers call this, this calls the Repository.
 *
 * Day 3 — Java Developer 1
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IncidentService {

    private final IncidentRepository incidentRepository;

    // ── Valid values for validation ───────────────────────────────
    private static final List<String> VALID_SEVERITIES =
            List.of("LOW", "MEDIUM", "HIGH", "CRITICAL");

    private static final List<String> VALID_STATUSES =
            List.of("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED");

    // ── CREATE ────────────────────────────────────────────────────

    /**
     * Create a new incident.
     * Validates input before saving.
     */
    public Incident createIncident(Incident incident) {
        log.info("Creating new incident: {}", incident.getTitle());

        // Input validation
        validateIncident(incident);

        // Set default status if not provided
        if (incident.getStatus() == null || incident.getStatus().isBlank()) {
            incident.setStatus("OPEN");
        }

        // Set incident date to now if not provided
        if (incident.getIncidentDate() == null) {
            incident.setIncidentDate(LocalDateTime.now());
        }

        Incident saved = incidentRepository.save(incident);
        log.info("Incident created with id: {}", saved.getId());
        return saved;
    }

    // ── READ ──────────────────────────────────────────────────────

    /**
     * Get all incidents — paginated.
     * Used by GET /all endpoint.
     */
    @Transactional(readOnly = true)
    public Page<Incident> getAllIncidents(Pageable pageable) {
        log.info("Fetching all incidents - page: {}", pageable.getPageNumber());
        return incidentRepository.findAllByIsDeletedFalse(pageable);
    }

    /**
     * Get one incident by ID.
     * Throws 404 if not found.
     */
    @Transactional(readOnly = true)
    public Incident getIncidentById(Long id) {
        log.info("Fetching incident with id: {}", id);
        return incidentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", id));
    }

    /**
     * Search incidents by keyword.
     * Used by GET /search?q= endpoint.
     */
    @Transactional(readOnly = true)
    public Page<Incident> searchIncidents(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            throw new BadRequestException("Search query cannot be empty");
        }
        log.info("Searching incidents with query: {}", query);
        return incidentRepository.searchIncidents(query.trim(), pageable);
    }

    /**
     * Get incidents by status.
     */
    @Transactional(readOnly = true)
    public Page<Incident> getIncidentsByStatus(String status, Pageable pageable) {
        validateStatus(status);
        return incidentRepository.findAllByStatusAndIsDeletedFalse(
                status.toUpperCase(), pageable);
    }

    // ── UPDATE ────────────────────────────────────────────────────

    /**
     * Update an existing incident.
     * Throws 404 if not found.
     */
    public Incident updateIncident(Long id, Incident updatedIncident) {
        log.info("Updating incident with id: {}", id);

        // Find existing incident
        Incident existing = incidentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", id));

        // Validate updated fields
        validateIncident(updatedIncident);

        // Update fields
        existing.setTitle(updatedIncident.getTitle());
        existing.setDescription(updatedIncident.getDescription());
        existing.setIncidentType(updatedIncident.getIncidentType());
        existing.setSeverity(updatedIncident.getSeverity().toUpperCase());
        existing.setStatus(updatedIncident.getStatus().toUpperCase());
        existing.setAssignedTo(updatedIncident.getAssignedTo());
        existing.setLocation(updatedIncident.getLocation());
        existing.setEvidenceUrl(updatedIncident.getEvidenceUrl());
        existing.setIncidentDate(updatedIncident.getIncidentDate());

        // Set resolved date if status changed to RESOLVED
        if ("RESOLVED".equals(updatedIncident.getStatus().toUpperCase())
                && existing.getResolvedDate() == null) {
            existing.setResolvedDate(LocalDateTime.now());
        }

        Incident saved = incidentRepository.save(existing);
        log.info("Incident updated: {}", id);
        return saved;
    }

    // ── DELETE (soft) ─────────────────────────────────────────────

    /**
     * Soft delete — sets isDeleted = true, does NOT remove from DB.
     * This way we keep audit history.
     */
    public void deleteIncident(Long id) {
        log.info("Soft deleting incident with id: {}", id);

        Incident incident = incidentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", id));

        incident.setIsDeleted(true);
        incidentRepository.save(incident);
        log.info("Incident soft deleted: {}", id);
    }

    // ── STATS (for Dashboard KPI cards) ───────────────────────────

    /**
     * Get counts for dashboard.
     */
    @Transactional(readOnly = true)
    public DashboardStats getStats() {
        return new DashboardStats(
                incidentRepository.countByIsDeletedFalse(),
                incidentRepository.countByStatusAndIsDeletedFalse("OPEN"),
                incidentRepository.countByStatusAndIsDeletedFalse("IN_PROGRESS"),
                incidentRepository.countByStatusAndIsDeletedFalse("RESOLVED"),
                incidentRepository.countBySeverityAndIsDeletedFalse("CRITICAL"),
                incidentRepository.countByCreatedAtAfterAndIsDeletedFalse(
                        LocalDateTime.now().minusDays(7))
        );
    }

    // ── VALIDATION ────────────────────────────────────────────────

    private void validateIncident(Incident incident) {
        if (incident.getTitle() == null || incident.getTitle().isBlank()) {
            throw new BadRequestException("Title is required");
        }
        if (incident.getTitle().length() > 255) {
            throw new BadRequestException("Title must be under 255 characters");
        }
        if (incident.getDescription() == null || incident.getDescription().isBlank()) {
            throw new BadRequestException("Description is required");
        }
        if (incident.getIncidentType() == null || incident.getIncidentType().isBlank()) {
            throw new BadRequestException("Incident type is required");
        }
        if (incident.getReportedBy() == null || incident.getReportedBy().isBlank()) {
            throw new BadRequestException("Reporter name is required");
        }
        if (incident.getSeverity() != null) {
            validateSeverity(incident.getSeverity());
        }
        if (incident.getStatus() != null) {
            validateStatus(incident.getStatus());
        }
    }

    private void validateSeverity(String severity) {
        if (!VALID_SEVERITIES.contains(severity.toUpperCase())) {
            throw new BadRequestException(
                    "Invalid severity. Must be one of: " + VALID_SEVERITIES);
        }
    }

    private void validateStatus(String status) {
        if (!VALID_STATUSES.contains(status.toUpperCase())) {
            throw new BadRequestException(
                    "Invalid status. Must be one of: " + VALID_STATUSES);
        }
    }

    // ── Inner class for dashboard stats ──────────────────────────

    public record DashboardStats(
            long total,
            long open,
            long inProgress,
            long resolved,
            long critical,
            long thisWeek
    ) {}
}
