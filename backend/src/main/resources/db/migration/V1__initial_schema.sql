-- ─────────────────────────────────────────────────────────────────────────────
-- V1: Initial schema for Intrastat Demonstrator
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE legal_entities (
    id           BIGSERIAL    PRIMARY KEY,
    name         VARCHAR(200) NOT NULL,
    country_code CHAR(2)      NOT NULL,
    vat_number   VARCHAR(50),
    active       BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE eu_countries (
    code          CHAR(2)      PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    active_member BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE parts (
    id                 BIGSERIAL     PRIMARY KEY,
    part_code          VARCHAR(20)   NOT NULL UNIQUE,
    name               VARCHAR(200)  NOT NULL,
    cn8_code           CHAR(8)       NOT NULL,
    supplementary_unit VARCHAR(10),
    unit_mass_kg       NUMERIC(8,2)  NOT NULL,
    unit_price_eur     NUMERIC(10,2) NOT NULL
);

-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE transactions (
    id                         BIGSERIAL     PRIMARY KEY,
    legal_entity_id            BIGINT        NOT NULL REFERENCES legal_entities(id),
    period                     DATE          NOT NULL,   -- stored as first day of month
    flow_type                  VARCHAR(10)   NOT NULL CHECK (flow_type IN ('ARRIVAL', 'DISPATCH')),
    counterpart_country_code   CHAR(2)       NOT NULL REFERENCES eu_countries(code),
    part_id                    BIGINT        NOT NULL REFERENCES parts(id),
    quantity                   INTEGER       NOT NULL CHECK (quantity > 0),
    net_mass_kg                NUMERIC(10,2) NOT NULL,
    statistical_value_eur      NUMERIC(12,2) NOT NULL,
    invoice_value_eur          NUMERIC(12,2) NOT NULL,
    nature_of_transaction_code SMALLINT      NOT NULL CHECK (nature_of_transaction_code BETWEEN 11 AND 99),
    mode_of_transport          SMALLINT      NOT NULL CHECK (mode_of_transport BETWEEN 1 AND 9),
    delivery_terms             VARCHAR(5)    NOT NULL,
    created_at                 TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at                 TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_txn_legal_entity  ON transactions(legal_entity_id);
CREATE INDEX idx_txn_period        ON transactions(period);
CREATE INDEX idx_txn_flow_type     ON transactions(flow_type);
CREATE INDEX idx_txn_part          ON transactions(part_id);
CREATE INDEX idx_txn_counterpart   ON transactions(counterpart_country_code);

-- Auto-update updated_at on every row modification
CREATE OR REPLACE FUNCTION fn_update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_transactions_updated_at
    BEFORE UPDATE ON transactions
    FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();
