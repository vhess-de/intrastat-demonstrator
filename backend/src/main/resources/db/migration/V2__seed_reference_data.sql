-- ─────────────────────────────────────────────────────────────────────────────
-- V2: Seed reference data — legal entities, EU countries, automotive parts
-- ─────────────────────────────────────────────────────────────────────────────

-- ── Legal Entities ────────────────────────────────────────────────────────────
INSERT INTO legal_entities (name, country_code, vat_number, active) VALUES
    ('Ford Werke GmbH',      'DE', 'DE812156231',       TRUE),
    ('Ford Romania S.A.',    'RO', 'RO14388255',        TRUE),
    ('Ford Nederland B.V.',  'NL', 'NL004495445B01',    TRUE);

-- ── EU Member States (27) ─────────────────────────────────────────────────────
INSERT INTO eu_countries (code, name, active_member) VALUES
    ('AT', 'Austria',        TRUE),
    ('BE', 'Belgium',        TRUE),
    ('BG', 'Bulgaria',       TRUE),
    ('CY', 'Cyprus',         TRUE),
    ('CZ', 'Czechia',        TRUE),
    ('DE', 'Germany',        TRUE),
    ('DK', 'Denmark',        TRUE),
    ('EE', 'Estonia',        TRUE),
    ('ES', 'Spain',          TRUE),
    ('FI', 'Finland',        TRUE),
    ('FR', 'France',         TRUE),
    ('GR', 'Greece',         TRUE),
    ('HR', 'Croatia',        TRUE),
    ('HU', 'Hungary',        TRUE),
    ('IE', 'Ireland',        TRUE),
    ('IT', 'Italy',          TRUE),
    ('LT', 'Lithuania',      TRUE),
    ('LU', 'Luxembourg',     TRUE),
    ('LV', 'Latvia',         TRUE),
    ('MT', 'Malta',          TRUE),
    ('NL', 'Netherlands',    TRUE),
    ('PL', 'Poland',         TRUE),
    ('PT', 'Portugal',       TRUE),
    ('RO', 'Romania',        TRUE),
    ('SE', 'Sweden',         TRUE),
    ('SI', 'Slovenia',       TRUE),
    ('SK', 'Slovakia',       TRUE);

-- ── Automotive Parts (20) — CN8 codes, unit masses and prices ─────────────────
-- unit_mass_kg: realistic part weight per piece
-- unit_price_eur: realistic transfer price per piece
INSERT INTO parts (part_code, name, cn8_code, supplementary_unit, unit_mass_kg, unit_price_eur) VALUES
    ('P-001', 'Engine Block (2.0L Diesel)',           '84082020', 'p/st', 180.00,  4200.00),
    ('P-002', 'Gearbox Assembly (6-speed manual)',    '87084020', 'p/st',  45.00,  1800.00),
    ('P-003', 'Brake Disc Set (front axle)',          '87083010', 'p/st',  12.00,   180.00),
    ('P-004', 'Front Bumper Assembly',                '87081010', 'p/st',   8.00,   320.00),
    ('P-005', 'Alternator 180A',                      '85115019', 'p/st',   4.50,   240.00),
    ('P-006', 'Lithium-Ion Battery Pack (48V)',        '85076000', 'p/st',  22.00,  1200.00),
    ('P-007', 'Radiator (coolant)',                   '87089120', 'p/st',   6.50,   280.00),
    ('P-008', 'Exhaust System (complete)',             '87089220', 'p/st',  14.00,   420.00),
    ('P-009', 'Clutch Kit',                           '87089310', 'p/st',   8.50,   360.00),
    ('P-010', 'Front Shock Absorber (pair)',           '87088020', 'p/st',  11.00,   290.00),
    ('P-011', 'Drive Shaft (rear)',                   '84831095', 'p/st',   9.00,   340.00),
    ('P-012', 'Seat Belt Assembly',                   '87082110', 'p/st',   1.80,    95.00),
    ('P-013', 'Wiring Harness (engine bay)',           '85443090', 'p/st',   5.50,   185.00),
    ('P-014', 'Steering Column Assembly',              '87089435', 'p/st',   7.00,   520.00),
    ('P-015', 'Fuel Pump (high pressure)',             '84133020', 'p/st',   1.20,   165.00),
    ('P-016', 'Dashboard Trim Panel',                 '87082991', 'p/st',   4.20,   145.00),
    ('P-017', 'Alloy Wheel Rim 17"',                  '87087010', 'p/st',   9.50,   210.00),
    ('P-018', 'Airbag Module (driver)',                '87082910', 'p/st',   1.50,   380.00),
    ('P-019', 'Power Steering Unit (electric)',        '87089410', 'p/st',   5.80,   680.00),
    ('P-020', 'Catalytic Converter',                  '85433000', 'p/st',   4.80,   490.00);
