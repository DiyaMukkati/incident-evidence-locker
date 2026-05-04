-- ================================================================
-- V2__audit_log.sql — Tool-36 Incident Evidence Locker
-- Flyway migration — audit log table
-- Java Developer 2 — Day 3
-- ================================================================

CREATE TABLE audit_log (
    id            BIGSERIAL       PRIMARY KEY,
    action        VARCHAR(20)     NOT NULL,  -- CREATE, UPDATE, DELETE
    entity_type   VARCHAR(50)     NOT NULL,  -- e.g. "Incident"
    entity_id     BIGINT          NOT NULL,  -- ID of the record changed
    changed_by    VARCHAR(100)    NOT NULL,  -- username who made the change
    details       TEXT,                      -- what changed (JSON string)
    created_at    TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_created_at ON audit_log(created_at);
