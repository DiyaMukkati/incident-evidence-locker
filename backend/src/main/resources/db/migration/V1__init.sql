-- ================================================================
-- V1__init.sql — Tool-36 Incident Evidence Locker
-- Flyway migration — runs automatically on app startup
-- Java Developer 2 owns this file
-- ================================================================

-- ── Create incidents table ────────────────────────────────────────
CREATE TABLE incidents (
    id               BIGSERIAL       PRIMARY KEY,

    -- Core fields
    title            VARCHAR(255)    NOT NULL,
    description      TEXT            NOT NULL,
    incident_type    VARCHAR(100)    NOT NULL,
    severity         VARCHAR(20)     NOT NULL CHECK (severity IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    status           VARCHAR(20)     NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN','IN_PROGRESS','RESOLVED','CLOSED')),
    reported_by      VARCHAR(100)    NOT NULL,
    assigned_to      VARCHAR(100),
    location         VARCHAR(255),
    evidence_url     VARCHAR(500),
    incident_date    TIMESTAMP       NOT NULL,
    resolved_date    TIMESTAMP,

    -- AI generated fields
    ai_description   TEXT,
    ai_recommendation TEXT,
    ai_generated_at  TIMESTAMP,

    -- Soft delete
    is_deleted       BOOLEAN         NOT NULL DEFAULT FALSE,

    -- Audit fields (auto-managed by Spring)
    created_at       TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- ── Indexes for fast queries ──────────────────────────────────────
CREATE INDEX idx_incidents_status     ON incidents(status)     WHERE is_deleted = FALSE;
CREATE INDEX idx_incidents_severity   ON incidents(severity)   WHERE is_deleted = FALSE;
CREATE INDEX idx_incidents_reported_by ON incidents(reported_by) WHERE is_deleted = FALSE;
CREATE INDEX idx_incidents_assigned_to ON incidents(assigned_to) WHERE is_deleted = FALSE;
CREATE INDEX idx_incidents_incident_date ON incidents(incident_date) WHERE is_deleted = FALSE;
CREATE INDEX idx_incidents_created_at ON incidents(created_at) WHERE is_deleted = FALSE;
CREATE INDEX idx_incidents_is_deleted ON incidents(is_deleted);
