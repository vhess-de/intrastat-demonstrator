-- ─────────────────────────────────────────────────────────────────────────────
-- V3: Seed 60 transactions across January–March 2025
--     20 transactions per period, mix of ARRIVAL and DISPATCH
--     Origins: Ford Werke GmbH (DE), Ford Romania S.A. (RO), Ford Nederland B.V. (NL)
--     Counterpart countries: FR, PL, IT, ES, BE, CZ, HU, AT, SE, SK, DE
--     All 20 parts appear each period
--     NOTc=21 (returns): at least 3 total
--     Mode of transport 4 (air): at least 4 total
-- ─────────────────────────────────────────────────────────────────────────────
DO $$
DECLARE
    -- Legal entity IDs (inserted in V2, looked up by name for safety)
    le1 BIGINT;   -- Ford Werke GmbH (DE)
    le2 BIGINT;   -- Ford Romania S.A. (RO)
    le3 BIGINT;   -- Ford Nederland B.V. (NL)

    -- Part IDs (looked up by part_code)
    p01 BIGINT;  p02 BIGINT;  p03 BIGINT;  p04 BIGINT;  p05 BIGINT;
    p06 BIGINT;  p07 BIGINT;  p08 BIGINT;  p09 BIGINT;  p10 BIGINT;
    p11 BIGINT;  p12 BIGINT;  p13 BIGINT;  p14 BIGINT;  p15 BIGINT;
    p16 BIGINT;  p17 BIGINT;  p18 BIGINT;  p19 BIGINT;  p20 BIGINT;

    ts TIMESTAMP := NOW();
BEGIN
    -- Resolve legal entity IDs
    SELECT id INTO le1 FROM legal_entities WHERE name = 'Ford Werke GmbH';
    SELECT id INTO le2 FROM legal_entities WHERE name = 'Ford Romania S.A.';
    SELECT id INTO le3 FROM legal_entities WHERE name = 'Ford Nederland B.V.';

    -- Resolve part IDs
    SELECT id INTO p01 FROM parts WHERE part_code = 'P-001';
    SELECT id INTO p02 FROM parts WHERE part_code = 'P-002';
    SELECT id INTO p03 FROM parts WHERE part_code = 'P-003';
    SELECT id INTO p04 FROM parts WHERE part_code = 'P-004';
    SELECT id INTO p05 FROM parts WHERE part_code = 'P-005';
    SELECT id INTO p06 FROM parts WHERE part_code = 'P-006';
    SELECT id INTO p07 FROM parts WHERE part_code = 'P-007';
    SELECT id INTO p08 FROM parts WHERE part_code = 'P-008';
    SELECT id INTO p09 FROM parts WHERE part_code = 'P-009';
    SELECT id INTO p10 FROM parts WHERE part_code = 'P-010';
    SELECT id INTO p11 FROM parts WHERE part_code = 'P-011';
    SELECT id INTO p12 FROM parts WHERE part_code = 'P-012';
    SELECT id INTO p13 FROM parts WHERE part_code = 'P-013';
    SELECT id INTO p14 FROM parts WHERE part_code = 'P-014';
    SELECT id INTO p15 FROM parts WHERE part_code = 'P-015';
    SELECT id INTO p16 FROM parts WHERE part_code = 'P-016';
    SELECT id INTO p17 FROM parts WHERE part_code = 'P-017';
    SELECT id INTO p18 FROM parts WHERE part_code = 'P-018';
    SELECT id INTO p19 FROM parts WHERE part_code = 'P-019';
    SELECT id INTO p20 FROM parts WHERE part_code = 'P-020';

    -- ─────────────────────────────────────────────────────────────────────────
    -- JANUARY 2025  (13 dispatches, 7 arrivals)
    -- Columns: legal_entity_id, period, flow_type, counterpart_country_code,
    --          part_id, quantity, net_mass_kg, statistical_value_eur,
    --          invoice_value_eur, nature_of_transaction_code,
    --          mode_of_transport, delivery_terms, created_at, updated_at
    -- ─────────────────────────────────────────────────────────────────────────
    INSERT INTO transactions VALUES
    -- #1  Ford DE → FR  Gearbox  50 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-01-01','DISPATCH','FR', p02,  50,   2250.00,  90000.00,  89500.00, 11, 3,'DAP',ts,ts),
    -- #2  Ford DE → PL  Brake Discs  200 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-01-01','DISPATCH','PL', p03, 200,   2400.00,  36000.00,  36200.00, 11, 3,'EXW',ts,ts),
    -- #3  Ford DE → BE  Alternator  100 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-01-01','DISPATCH','BE', p05, 100,    450.00,  24000.00,  23800.00, 11, 3,'FCA',ts,ts),
    -- #4  Ford DE ← IT  Bumper  40 pcs  air   NOTc=11
    (DEFAULT, le1,'2025-01-01','ARRIVAL', 'IT', p04,  40,    320.00,  12800.00,  12900.00, 11, 4,'DDP',ts,ts),
    -- #5  Ford DE ← FR  Wiring Harness  150 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-01-01','ARRIVAL', 'FR', p13, 150,    825.00,  27750.00,  27500.00, 11, 3,'FCA',ts,ts),
    -- #6  Ford DE → ES  Radiator  60 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-01-01','DISPATCH','ES', p07,  60,    390.00,  16800.00,  16900.00, 11, 3,'DAP',ts,ts),
    -- #7  Ford DE ← CZ  Dashboard Panel  80 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-01-01','ARRIVAL', 'CZ', p16,  80,    336.00,  11600.00,  11500.00, 11, 3,'EXW',ts,ts),
    -- #8  Ford DE → HU  Exhaust System  30 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-01-01','DISPATCH','HU', p08,  30,    420.00,  12600.00,  12700.00, 11, 3,'DAP',ts,ts),
    -- #9  Ford RO → DE  Engine Block  10 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-01-01','DISPATCH','DE', p01,  10,   1800.00,  42000.00,  42200.00, 11, 3,'FCA',ts,ts),
    -- #10 Ford RO → FR  Drive Shaft  80 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-01-01','DISPATCH','FR', p11,  80,    720.00,  27200.00,  27000.00, 11, 3,'DAP',ts,ts),
    -- #11 Ford RO → HU  Battery Pack  20 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-01-01','DISPATCH','HU', p06,  20,    440.00,  24000.00,  24100.00, 11, 3,'EXW',ts,ts),
    -- #12 Ford RO ← PL  Clutch Kit  60 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-01-01','ARRIVAL', 'PL', p09,  60,    510.00,  21600.00,  21500.00, 11, 3,'EXW',ts,ts),
    -- #13 Ford RO ← BE  Seat Belt  200 pcs  road  NOTc=21 (return)
    (DEFAULT, le2,'2025-01-01','ARRIVAL', 'BE', p12, 200,    360.00,  19000.00,  18900.00, 21, 3,'FCA',ts,ts),
    -- #14 Ford RO → AT  Steering Column  30 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-01-01','DISPATCH','AT', p14,  30,    210.00,  15600.00,  15700.00, 11, 3,'DAP',ts,ts),
    -- #15 Ford RO ← CZ  Shock Absorber  50 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-01-01','ARRIVAL', 'CZ', p10,  50,    550.00,  14500.00,  14400.00, 11, 3,'EXW',ts,ts),
    -- #16 Ford NL → SE  Fuel Pump  120 pcs  sea   NOTc=11
    (DEFAULT, le3,'2025-01-01','DISPATCH','SE', p15, 120,    144.00,  19800.00,  19900.00, 11, 1,'FCA',ts,ts),
    -- #17 Ford NL → SK  Airbag Module  40 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-01-01','DISPATCH','SK', p18,  40,     60.00,  15200.00,  15300.00, 11, 3,'DDP',ts,ts),
    -- #18 Ford NL ← DE  Alloy Wheel Rim  100 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-01-01','ARRIVAL', 'DE', p17, 100,    950.00,  21000.00,  20900.00, 11, 3,'EXW',ts,ts),
    -- #19 Ford NL → IT  Power Steering  25 pcs  air   NOTc=11
    (DEFAULT, le3,'2025-01-01','DISPATCH','IT', p19,  25,    145.00,  17000.00,  17100.00, 11, 4,'DDP',ts,ts),
    -- #20 Ford NL → PL  Catalytic Converter  30 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-01-01','DISPATCH','PL', p20,  30,    144.00,  14700.00,  14600.00, 11, 3,'DAP',ts,ts);

    -- ─────────────────────────────────────────────────────────────────────────
    -- FEBRUARY 2025  (13 dispatches, 7 arrivals)
    -- ─────────────────────────────────────────────────────────────────────────
    INSERT INTO transactions VALUES
    -- #21 Ford DE → FR  Battery Pack  15 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-02-01','DISPATCH','FR', p06,  15,    330.00,  18000.00,  18100.00, 11, 3,'DAP',ts,ts),
    -- #22 Ford DE → PL  Gearbox  40 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-02-01','DISPATCH','PL', p02,  40,   1800.00,  72000.00,  71800.00, 11, 3,'EXW',ts,ts),
    -- #23 Ford DE ← ES  Brake Discs  180 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-02-01','ARRIVAL', 'ES', p03, 180,   2160.00,  32400.00,  32200.00, 11, 3,'EXW',ts,ts),
    -- #24 Ford DE → BE  Engine Block  8 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-02-01','DISPATCH','BE', p01,   8,   1440.00,  33600.00,  33700.00, 11, 3,'FCA',ts,ts),
    -- #25 Ford DE ← AT  Shock Absorber  40 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-02-01','ARRIVAL', 'AT', p10,  40,    440.00,  11600.00,  11500.00, 11, 3,'EXW',ts,ts),
    -- #26 Ford DE → IT  Alternator  80 pcs  air   NOTc=11
    (DEFAULT, le1,'2025-02-01','DISPATCH','IT', p05,  80,    360.00,  19200.00,  19300.00, 11, 4,'DDP',ts,ts),
    -- #27 Ford DE ← CZ  Front Bumper  60 pcs  road  NOTc=21 (return)
    (DEFAULT, le1,'2025-02-01','ARRIVAL', 'CZ', p04,  60,    480.00,  19200.00,  19000.00, 21, 3,'FCA',ts,ts),
    -- #28 Ford RO → DE  Drive Shaft  60 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-02-01','DISPATCH','DE', p11,  60,    540.00,  20400.00,  20500.00, 11, 3,'FCA',ts,ts),
    -- #29 Ford RO → FR  Steering Column  20 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-02-01','DISPATCH','FR', p14,  20,    140.00,  10400.00,  10500.00, 11, 3,'DAP',ts,ts),
    -- #30 Ford RO → HU  Clutch Kit  50 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-02-01','DISPATCH','HU', p09,  50,    425.00,  18000.00,  17900.00, 11, 3,'EXW',ts,ts),
    -- #31 Ford RO ← BE  Radiator  70 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-02-01','ARRIVAL', 'BE', p07,  70,    455.00,  19600.00,  19500.00, 11, 3,'FCA',ts,ts),
    -- #32 Ford RO → AT  Exhaust System  35 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-02-01','DISPATCH','AT', p08,  35,    490.00,  14700.00,  14800.00, 11, 3,'DAP',ts,ts),
    -- #33 Ford RO → SK  Wiring Harness  100 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-02-01','DISPATCH','SK', p13, 100,    550.00,  18500.00,  18400.00, 11, 3,'DAP',ts,ts),
    -- #34 Ford RO ← PL  Dashboard Panel  90 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-02-01','ARRIVAL', 'PL', p16,  90,    378.00,  13050.00,  12900.00, 11, 3,'EXW',ts,ts),
    -- #35 Ford NL → SE  Seat Belt  250 pcs  sea   NOTc=11
    (DEFAULT, le3,'2025-02-01','DISPATCH','SE', p12, 250,    450.00,  23750.00,  23600.00, 11, 1,'FCA',ts,ts),
    -- #36 Ford NL → FR  Airbag Module  30 pcs  air   NOTc=11
    (DEFAULT, le3,'2025-02-01','DISPATCH','FR', p18,  30,     45.00,  11400.00,  11500.00, 11, 4,'DDP',ts,ts),
    -- #37 Ford NL ← DE  Fuel Pump  200 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-02-01','ARRIVAL', 'DE', p15, 200,    240.00,  33000.00,  32800.00, 11, 3,'EXW',ts,ts),
    -- #38 Ford NL → IT  Catalytic Converter  25 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-02-01','DISPATCH','IT', p20,  25,    120.00,  12250.00,  12300.00, 11, 3,'DAP',ts,ts),
    -- #39 Ford NL ← SK  Alloy Wheel Rim  80 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-02-01','ARRIVAL', 'SK', p17,  80,    760.00,  16800.00,  16700.00, 11, 3,'EXW',ts,ts),
    -- #40 Ford NL → PL  Power Steering  20 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-02-01','DISPATCH','PL', p19,  20,    116.00,  13600.00,  13700.00, 11, 3,'DAP',ts,ts);

    -- ─────────────────────────────────────────────────────────────────────────
    -- MARCH 2025  (13 dispatches, 7 arrivals)
    -- ─────────────────────────────────────────────────────────────────────────
    INSERT INTO transactions VALUES
    -- #41 Ford DE → FR  Brake Discs  250 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-03-01','DISPATCH','FR', p03, 250,   3000.00,  45000.00,  44800.00, 11, 3,'DAP',ts,ts),
    -- #42 Ford DE → PL  Battery Pack  18 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-03-01','DISPATCH','PL', p06,  18,    396.00,  21600.00,  21700.00, 11, 3,'EXW',ts,ts),
    -- #43 Ford DE ← ES  Gearbox  30 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-03-01','ARRIVAL', 'ES', p02,  30,   1350.00,  54000.00,  53800.00, 11, 3,'FCA',ts,ts),
    -- #44 Ford DE → BE  Alternator  90 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-03-01','DISPATCH','BE', p05,  90,    405.00,  21600.00,  21700.00, 11, 3,'DAP',ts,ts),
    -- #45 Ford DE ← AT  Exhaust System  40 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-03-01','ARRIVAL', 'AT', p08,  40,    560.00,  16800.00,  16700.00, 11, 3,'EXW',ts,ts),
    -- #46 Ford DE → IT  Front Bumper  50 pcs  air   NOTc=11
    (DEFAULT, le1,'2025-03-01','DISPATCH','IT', p04,  50,    400.00,  16000.00,  16100.00, 11, 4,'DDP',ts,ts),
    -- #47 Ford DE ← CZ  Wiring Harness  120 pcs  road  NOTc=11
    (DEFAULT, le1,'2025-03-01','ARRIVAL', 'CZ', p13, 120,    660.00,  22200.00,  22000.00, 11, 3,'EXW',ts,ts),
    -- #48 Ford RO → DE  Engine Block  12 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-03-01','DISPATCH','DE', p01,  12,   2160.00,  50400.00,  50600.00, 11, 3,'FCA',ts,ts),
    -- #49 Ford RO → FR  Clutch Kit  70 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-03-01','DISPATCH','FR', p09,  70,    595.00,  25200.00,  25100.00, 11, 3,'DAP',ts,ts),
    -- #50 Ford RO → HU  Drive Shaft  60 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-03-01','DISPATCH','HU', p11,  60,    540.00,  20400.00,  20500.00, 11, 3,'EXW',ts,ts),
    -- #51 Ford RO ← BE  Shock Absorber  45 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-03-01','ARRIVAL', 'BE', p10,  45,    495.00,  13050.00,  12900.00, 11, 3,'FCA',ts,ts),
    -- #52 Ford RO → AT  Radiator  55 pcs  road  NOTc=11
    (DEFAULT, le2,'2025-03-01','DISPATCH','AT', p07,  55,    357.50,  15400.00,  15500.00, 11, 3,'DAP',ts,ts),
    -- #53 Ford RO ← PL  Seat Belt  300 pcs  road  NOTc=21 (return)
    (DEFAULT, le2,'2025-03-01','ARRIVAL', 'PL', p12, 300,    540.00,  28500.00,  28300.00, 21, 3,'FCA',ts,ts),
    -- #54 Ford RO → SK  Steering Column  25 pcs  air   NOTc=11
    (DEFAULT, le2,'2025-03-01','DISPATCH','SK', p14,  25,    175.00,  13000.00,  13100.00, 11, 4,'DDP',ts,ts),
    -- #55 Ford NL → SE  Dashboard Panel  100 pcs  sea   NOTc=11
    (DEFAULT, le3,'2025-03-01','DISPATCH','SE', p16, 100,    420.00,  14500.00,  14600.00, 11, 1,'FCA',ts,ts),
    -- #56 Ford NL → FR  Fuel Pump  150 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-03-01','DISPATCH','FR', p15, 150,    180.00,  24750.00,  24600.00, 11, 3,'DAP',ts,ts),
    -- #57 Ford NL ← IT  Alloy Wheel Rim  120 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-03-01','ARRIVAL', 'IT', p17, 120,   1140.00,  25200.00,  25000.00, 11, 3,'EXW',ts,ts),
    -- #58 Ford NL → SK  Airbag Module  35 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-03-01','DISPATCH','SK', p18,  35,     52.50,  13300.00,  13400.00, 11, 3,'DDP',ts,ts),
    -- #59 Ford NL ← DE  Power Steering  20 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-03-01','ARRIVAL', 'DE', p19,  20,    116.00,  13600.00,  13500.00, 11, 3,'FCA',ts,ts),
    -- #60 Ford NL → PL  Catalytic Converter  40 pcs  road  NOTc=11
    (DEFAULT, le3,'2025-03-01','DISPATCH','PL', p20,  40,    192.00,  19600.00,  19700.00, 11, 3,'DAP',ts,ts);

END;
$$;

-- ─────────────────────────────────────────────────────────────────────────────
-- Seed summary
--   60 transactions total: 20 per period (Jan, Feb, Mar 2025)
--   Per period: 13 DISPATCH, 7 ARRIVAL
--   NOTc = 21 (returns): txn #13, #27, #53  (3 total ✓)
--   Mode 4 (air freight): txn #4, #19, #26, #36, #46, #54  (6 total ✓)
--   Mode 1 (sea):  txn #16, #35, #55  (3 total)
--   All 20 parts appear in each period  ✓
--   Counterpart countries used: DE, FR, PL, BE, IT, ES, HU, CZ, AT, SE, SK  ✓
-- ─────────────────────────────────────────────────────────────────────────────
