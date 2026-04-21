# Intrastat Demonstrator — Product Requirements Document

## Overview

A self-contained demo application showing Intrastat trade reporting for Ford of Europe. Three Ford/generic legal entities (Germany, Romania, Netherlands) ship automotive parts to and from various EU countries. The app displays 3 months of pre-seeded transaction data, allows inline editing of key fields, and renders an aggregated Intrastat report view — all running on a VPS via Docker Compose with no login required.

---

## Tech Stack

| Layer | Choice |
|-------|--------|
| Backend language | Java 21 |
| Backend framework | Spring Boot 3.x |
| API style | REST + OpenAPI (Springdoc) |
| Database | PostgreSQL 17 |
| ORM / migrations | Spring Data JPA + Flyway |
| Frontend | Angular 18 (standalone components, Angular Material) |
| Frontend server | Nginx (serves Angular build, proxies `/api` to backend) |
| Containers | Docker Compose: `db`, `backend`, `frontend` |
| Hosting | VPS (any Linux host with Docker) |

---

## Seed Data Specification

### Reporting Periods
January 2025, February 2025, March 2025 — approximately 20 transactions per month (60 total).

### Legal Entities (Senders / Receivers)

| ID | Name | Country | VAT Number |
|----|------|---------|------------|
| LE-1 | Ford Werke GmbH | DE | DE812156231 |
| LE-2 | Ford Romania S.A. | RO | RO14388255 |
| LE-3 | Ford Nederland B.V. | NL | NL004495445B01 |

### Destination / Origin Countries (ship-to / ship-from)
FR, PL, IT, ES, BE, CZ, HU, AT, SE, SK — used as counterpart countries in both dispatch and arrival transactions.

### Flow Types
- **DISPATCH** — goods sent from a Ford entity (DE/RO/NL) to another EU country.
- **ARRIVAL** — goods received by a Ford entity (DE/RO/NL) from another EU country.

### Automotive Parts (20 parts)

| Part Code | Part Name | CN8 Code | Supplementary Unit |
|-----------|-----------|----------|--------------------|
| P-001 | Engine Block (2.0L Diesel) | 84082020 | p/st |
| P-002 | Gearbox Assembly (6-speed manual) | 87084020 | p/st |
| P-003 | Brake Disc Set (front axle) | 87083010 | p/st |
| P-004 | Front Bumper Assembly | 87081010 | p/st |
| P-005 | Alternator 180A | 85115019 | p/st |
| P-006 | Lithium-Ion Battery Pack (48V) | 85076000 | p/st |
| P-007 | Radiator (coolant) | 87089120 | p/st |
| P-008 | Exhaust System (complete) | 87089220 | p/st |
| P-009 | Clutch Kit | 87089310 | p/st |
| P-010 | Front Shock Absorber (pair) | 87088020 | p/st |
| P-011 | Drive Shaft (rear) | 84831095 | p/st |
| P-012 | Seat Belt Assembly | 87082110 | p/st |
| P-013 | Wiring Harness (engine bay) | 85443090 | p/st |
| P-014 | Steering Column Assembly | 87089435 | p/st |
| P-015 | Fuel Pump (high pressure) | 84133020 | p/st |
| P-016 | Dashboard Trim Panel | 87082991 | p/st |
| P-017 | Alloy Wheel Rim 17" | 87087010 | p/st |
| P-018 | Airbag Module (driver) | 87082910 | p/st |
| P-019 | Power Steering Unit (electric) | 87089410 | p/st |
| P-020 | Catalytic Converter | 85433000 | p/st |

### Transaction Field Ranges (for seed generation)

| Field | Range |
|-------|-------|
| Quantity (supplementary) | 10 – 500 units |
| Net mass | derived: `quantity × part_unit_mass_kg` |
| Statistical value (EUR) | derived: `quantity × unit_price_eur` |
| Invoice value (EUR) | same as statistical value ± 0–5% |
| Nature of transaction code | 11 (outright purchase/sale) for most; some 21 (return) |
| Mode of transport | 3 (road) for most; some 4 (air), 1 (sea) |
| Delivery terms | EXW, FCA, DAP, DDP (varied across transactions) |

---

## Epics

1. Project Setup & Infrastructure
2. Reference Data & Seed Data
3. Transaction Management API
4. Intrastat Report API
5. Angular Frontend — Transaction Table & Inline Edit
6. Angular Frontend — Intrastat Report View

---

## User Stories

---

### Epic 1 — Project Setup & Infrastructure

#### US-01 — Docker Compose Stack

**As a** developer,
**I want** a single `docker-compose.yml` that starts the entire application,
**so that** deploying on any VPS is a one-command operation.

**Acceptance Criteria**

- Three services defined: `db` (PostgreSQL 17), `backend` (Spring Boot JAR), `frontend` (Nginx serving Angular build + reverse proxy).
- `backend` depends on `db`; starts only after the database is healthy.
- `frontend` proxies all requests to `/api/**` to `backend:8080`.
- A named volume `pgdata` persists the PostgreSQL data directory across restarts.
- `docker compose up -d` brings the full stack online on a fresh machine.
- A `.env.example` documents every configurable variable (`POSTGRES_PASSWORD`, `POSTGRES_DB`, `SPRING_PROFILES_ACTIVE`).
- `docker compose down` stops all services; `docker compose down -v` also removes volumes.

---

#### US-02 — Backend Scaffold

**As a** developer,
**I want** a runnable Spring Boot application connected to PostgreSQL,
**so that** subsequent epics can add features on a working foundation.

**Acceptance Criteria**

- Spring Boot 3.x project with packages: `controller`, `service`, `repository`, `model`, `dto`.
- `GET /api/health` returns `{ "status": "UP" }` with HTTP 200.
- Application reads `spring.datasource.*` from environment variables.
- Flyway runs all migrations automatically on startup before serving requests.
- Springdoc OpenAPI available at `GET /api/v3/api-docs` and Swagger UI at `/api/swagger-ui.html`.
- Multi-stage Dockerfile: Maven build stage → Eclipse Temurin 21 JRE runtime stage.

---

#### US-03 — Frontend Scaffold

**As a** developer,
**I want** an Angular 18 SPA with Angular Material and a shell layout,
**so that** feature modules can be added on top.

**Acceptance Criteria**

- Angular Material theme applied (Ford-inspired: dark blue `#003078` primary, white surface).
- Shell: top app bar with "Intrastat Demonstrator" title and Ford wordmark, left side navigation with items: **Transactions**, **Intrastat Report**.
- Active route highlighted in navigation.
- Angular `HttpClient` base URL read from `environment.ts` (`/api`).
- Nginx `default.conf` serves `dist/` and proxies `/api` to `backend:8080`; all non-asset routes return `index.html` (SPA routing).
- Dockerfile: Node build stage (ng build) → Nginx runtime stage.

---

### Epic 2 — Reference Data & Seed Data

#### US-04 — Database Schema

**As a** developer,
**I want** Flyway migrations to create all tables,
**so that** the schema is version-controlled and reproducible.

**Acceptance Criteria**

Migration `V1__initial_schema.sql` creates:

```
legal_entities (id, name, country_code, vat_number, active)
eu_countries   (code PK, name, active_member)
parts          (id, part_code, name, cn8_code, supplementary_unit,
                unit_mass_kg, unit_price_eur)
transactions   (id, legal_entity_id FK, period DATE,
                flow_type VARCHAR CHECK IN ('ARRIVAL','DISPATCH'),
                counterpart_country_code FK eu_countries,
                part_id FK, quantity INTEGER, net_mass_kg NUMERIC,
                statistical_value_eur NUMERIC, invoice_value_eur NUMERIC,
                nature_of_transaction_code SMALLINT,
                mode_of_transport SMALLINT,
                delivery_terms VARCHAR(5),
                created_at TIMESTAMP, updated_at TIMESTAMP)
```

All foreign keys have corresponding indexes. `updated_at` updated automatically via a Flyway-created trigger or application-layer logic.

---

#### US-05 — Reference Data Seed

**As a** developer,
**I want** all reference tables populated by a Flyway migration,
**so that** the app is usable immediately after `docker compose up`.

**Acceptance Criteria**

Migration `V2__seed_reference_data.sql` inserts:
- 3 legal entities (Ford Werke GmbH DE, Ford Romania S.A. RO, Ford Nederland B.V. NL) as specified in the seed data table above.
- All 27 EU member states in `eu_countries`.
- All 20 automotive parts as specified in the parts table above, with realistic `unit_mass_kg` and `unit_price_eur` values (e.g. Engine Block: 180 kg, €4 200).

---

#### US-06 — Transaction Seed Data

**As a** developer,
**I want** 60 realistic transactions spread across January–March 2025,
**so that** the demo shows meaningful data without manual entry.

**Acceptance Criteria**

Migration `V3__seed_transactions.sql` inserts exactly 60 transactions:
- 20 transactions per period (January, February, March 2025).
- Each period includes a mix of ARRIVAL and DISPATCH flows (roughly 60/40 split).
- Sender/receiver legal entities: distributed across LE-1 (DE), LE-2 (RO), LE-3 (NL).
- Counterpart countries: at least 8 of the 10 destination countries used across all periods (FR, PL, IT, ES, BE, CZ, HU, AT, SE, SK).
- All 20 parts appear at least once across the 60 records.
- Quantities, values, transport modes, and delivery terms varied as per seed ranges.
- At least 3 transactions use nature_of_transaction_code = 21 (returns).
- At least 4 transactions use mode_of_transport = 4 (air freight).

---

### Epic 3 — Transaction Management API

#### US-07 — List Transactions

**As a** frontend developer,
**I want** a paginated, filterable list endpoint for transactions,
**so that** the Angular table can display and filter data efficiently.

**Acceptance Criteria**

`GET /api/transactions` supports query parameters:

| Parameter | Type | Description |
|-----------|------|-------------|
| `period` | `YYYY-MM` | Filter by reporting period |
| `flowType` | `ARRIVAL` \| `DISPATCH` | Filter by flow direction |
| `legalEntityId` | Long | Filter by sender/receiver entity |
| `counterpartCountry` | ISO-2 | Filter by counterpart country |
| `page` | int (default 0) | Zero-based page index |
| `size` | int (default 50) | Page size |

Response body:
```json
{
  "content": [ { ...transaction } ],
  "totalElements": 60,
  "totalPages": 2,
  "page": 0,
  "size": 50
}
```

Each transaction object includes all fields plus nested `legalEntity` (id, name, countryCode) and `part` (id, partCode, name, cn8Code).

---

#### US-08 — Update Transaction (Partial Edit)

**As a** frontend developer,
**I want** a PATCH endpoint to update selected fields of a transaction,
**so that** inline edits from the UI are persisted.

**Acceptance Criteria**

`PATCH /api/transactions/{id}` accepts a JSON body containing any subset of editable fields:

| Field | Type | Validation |
|-------|------|-----------|
| `quantity` | Integer | > 0 |
| `statisticalValueEur` | BigDecimal | > 0 |
| `invoiceValueEur` | BigDecimal | > 0 |
| `counterpartCountryCode` | String (ISO-2) | Must exist in `eu_countries` where `active_member = true` |
| `modeOfTransport` | Integer | 1–9 |
| `natureOfTransactionCode` | Integer | 11–99 |
| `deliveryTerms` | String | One of: EXW, FCA, CPT, CIP, DAP, DDP, FAS, FOB, CFR, CIF |

- `net_mass_kg` is recalculated automatically as `quantity × part.unit_mass_kg` whenever `quantity` changes.
- `updated_at` is set to the current timestamp on every successful PATCH.
- Unknown fields in the request body are ignored.
- Validation errors return HTTP 422 with body `{ "errors": [{ "field": "...", "message": "..." }] }`.
- Returns the full updated transaction object on success (HTTP 200).
- Non-existent transaction ID returns HTTP 404.

---

#### US-09 — Get Single Transaction

**As a** frontend developer,
**I want** a single-transaction endpoint,
**so that** the edit panel can load fresh data before opening.

**Acceptance Criteria**

`GET /api/transactions/{id}` returns the full transaction object including nested `legalEntity` and `part`.
Returns HTTP 404 if not found.

---

#### US-10 — List Reference Data (Supporting Endpoints)

**As a** frontend developer,
**I want** reference list endpoints for dropdowns,
**so that** Angular select fields are populated dynamically.

**Acceptance Criteria**

- `GET /api/legal-entities` — returns all 3 entities (id, name, countryCode).
- `GET /api/countries` — returns all EU member states (code, name) where `active_member = true`, sorted by name.
- `GET /api/parts` — returns all 20 parts (id, partCode, name, cn8Code).
- All three endpoints return HTTP 200 with a plain JSON array (no pagination needed).

---

### Epic 4 — Intrastat Report API

#### US-11 — Aggregated Intrastat Report

**As a** frontend developer,
**I want** an endpoint that aggregates transactions into Intrastat report lines,
**so that** the report view shows consolidated statistics without client-side computation.

**Acceptance Criteria**

`GET /api/report` supports query parameters:

| Parameter | Type | Description |
|-----------|------|-------------|
| `period` | `YYYY-MM` | Required. Reporting period. |
| `legalEntityId` | Long | Optional. Filter by entity. |
| `flowType` | `ARRIVAL` \| `DISPATCH` | Optional. Filter by flow. |

Aggregation groups transactions by: `(legalEntity, flowType, counterpartCountry, cn8Code, modeOfTransport, natureOfTransactionCode, deliveryTerms)`.

Response body:
```json
{
  "period": "2025-01",
  "generatedAt": "2025-04-21T10:00:00Z",
  "summary": {
    "totalLines": 14,
    "totalStatisticalValueEur": 284500.00,
    "totalNetMassKg": 18340.00,
    "totalQuantity": 620,
    "arrivalLines": 6,
    "dispatchLines": 8,
    "arrivalValueEur": 110200.00,
    "dispatchValueEur": 174300.00
  },
  "lines": [
    {
      "legalEntityId": 1,
      "legalEntityName": "Ford Werke GmbH",
      "legalEntityCountry": "DE",
      "flowType": "DISPATCH",
      "counterpartCountryCode": "FR",
      "counterpartCountryName": "France",
      "cn8Code": "87084020",
      "partName": "Gearbox Assembly (6-speed manual)",
      "totalQuantity": 120,
      "totalNetMassKg": 4800.00,
      "totalStatisticalValueEur": 96000.00,
      "totalInvoiceValueEur": 95500.00,
      "modeOfTransport": 3,
      "modeOfTransportLabel": "Road",
      "natureOfTransactionCode": 11,
      "deliveryTerms": "DAP",
      "transactionCount": 3
    }
  ]
}
```

- `modeOfTransportLabel` mapped from code: 1=Sea, 3=Road, 4=Air, 5=Post, 7=Fixed installations, 9=Own propulsion.
- Lines sorted by `flowType` ASC, then `totalStatisticalValueEur` DESC.
- If no transactions match the filters, returns an empty `lines` array with zeroed `summary`.

---

#### US-12 — Report Period Summary

**As a** frontend developer,
**I want** an endpoint listing available reporting periods,
**so that** the period selector in the UI is populated from real data.

**Acceptance Criteria**

`GET /api/report/periods` returns the distinct periods present in the transactions table, sorted descending:
```json
["2025-03", "2025-02", "2025-01"]
```

---

### Epic 5 — Angular Frontend: Transaction Table & Inline Edit

#### US-13 — Transaction List Table

**As a** user,
**I want** to see all transactions in a paginated, sortable table,
**so that** I can browse the full dataset.

**Acceptance Criteria**

- Page: `/transactions`.
- Angular Material table with columns:

| Column | Field |
|--------|-------|
| Period | `YYYY-MM` |
| Legal Entity | name + country flag emoji |
| Flow | `ARRIVAL` / `DISPATCH` badge (colour-coded: green / blue) |
| Counterpart Country | name + flag emoji |
| Part | partCode + name |
| CN8 Code | cn8Code |
| Quantity | right-aligned integer |
| Net Mass (kg) | right-aligned, 2 decimal places |
| Statistical Value (€) | right-aligned, formatted with thousands separator |
| Mode of Transport | label (Road / Air / Sea) |
| NOTc | integer |
| Delivery Terms | Incoterms code |
| Actions | Edit button |

- Client-side pagination: 20 rows per page; Angular Material paginator.
- All 60 rows loaded on initial page load (no server-side pagination required given the small dataset); filtering done client-side.
- Default sort: Period descending, then Statistical Value descending.

---

#### US-14 — Filter Bar

**As a** user,
**I want** to filter transactions by period, flow type, legal entity, and counterpart country,
**so that** I can focus on a specific subset of data.

**Acceptance Criteria**

- Filter bar above the table with four controls:
  - **Period** — `<mat-select>` populated from `GET /api/report/periods`; option "All periods".
  - **Flow Type** — `<mat-button-toggle-group>`: All | Arrivals | Dispatches.
  - **Legal Entity** — `<mat-select>` populated from `GET /api/legal-entities`; option "All entities".
  - **Counterpart Country** — `<mat-select>` populated from distinct countries in the loaded data; option "All countries".
- Filters apply immediately (no submit button).
- Active filter count shown as a badge on a "Clear Filters" button; button resets all filters.
- Transaction count shown below filter bar: "Showing X of 60 transactions".

---

#### US-15 — Inline Edit (Side Panel)

**As a** user,
**I want** to click Edit on a transaction and change selected fields in a side panel,
**so that** I can correct data without leaving the transactions page.

**Acceptance Criteria**

- Clicking Edit opens an Angular Material `<mat-sidenav>` on the right (400 px wide) with the transaction's current values pre-filled.
- Read-only fields shown as plain text (non-editable context): Legal Entity, Flow Type, Part Name, CN8 Code, Period.
- Editable fields with appropriate controls:

| Field | Control | Validation |
|-------|---------|-----------|
| Quantity | `<input type="number">` | Integer > 0 |
| Statistical Value (€) | `<input type="number">` | Decimal > 0 |
| Invoice Value (€) | `<input type="number">` | Decimal > 0 |
| Counterpart Country | `<mat-select>` (EU countries) | Required |
| Mode of Transport | `<mat-select>` (codes 1–9 with labels) | Required |
| Nature of Transaction Code | `<input type="number">` | Integer 11–99 |
| Delivery Terms | `<mat-select>` (Incoterms list) | Required |

- Net mass recalculated client-side on quantity change: displayed as "(estimated: X kg)" below the quantity field.
- **Save** button calls `PATCH /api/transactions/{id}`; on success: closes panel, shows snackbar "Transaction updated", refreshes row in table.
- **Cancel** button closes panel without saving; if form is dirty, shows a confirmation dialog "Discard changes?".
- Server-side validation errors displayed inline below the relevant field.
- Save button disabled while request is in flight; spinner shown in button.

---

### Epic 6 — Angular Frontend: Intrastat Report View

#### US-16 — Report Page & Period Selector

**As a** user,
**I want** to navigate to an Intrastat Report page and select a reporting period,
**so that** I can view the aggregated monthly report.

**Acceptance Criteria**

- Page: `/report`.
- Period selector: `<mat-select>` populated from `GET /api/report/periods`; defaults to most recent period on page load.
- Optional entity filter: `<mat-select>` with "All entities" default.
- Optional flow type toggle: All | Arrivals | Dispatches.
- Selecting any filter immediately reloads the report from `GET /api/report`.
- Loading state: skeleton rows shown while API call is in flight.

---

#### US-17 — Report Summary Cards

**As a** user,
**I want** to see high-level KPI cards at the top of the report page,
**so that** I can grasp the period's totals at a glance.

**Acceptance Criteria**

Six Angular Material cards arranged in a responsive row (2 columns on mobile, 6 on desktop):

| Card | Value |
|------|-------|
| Total Report Lines | `summary.totalLines` |
| Total Statistical Value | `€ summary.totalStatisticalValueEur` (formatted) |
| Total Net Mass | `summary.totalNetMassKg kg` |
| Total Quantity | `summary.totalQuantity units` |
| Arrivals Value | `€ summary.arrivalValueEur` |
| Dispatches Value | `€ summary.dispatchValueEur` |

- Cards use Ford blue header, white body.
- Values update whenever the report is reloaded.

---

#### US-18 — Report Lines Table

**As a** user,
**I want** to see the aggregated Intrastat report lines in a table,
**so that** I can review what would be submitted to the national authority.

**Acceptance Criteria**

- Angular Material table, not paginated (maximum ~20 aggregated lines per period).
- Columns:

| Column | Field |
|--------|-------|
| Legal Entity | name (country code) |
| Flow | badge (ARRIVAL / DISPATCH) |
| Counterpart Country | name + flag emoji |
| CN8 Code | cn8Code |
| Part Description | partName |
| Qty | totalQuantity |
| Net Mass (kg) | totalNetMassKg (2 dp) |
| Stat. Value (€) | totalStatisticalValueEur (formatted) |
| Invoice Value (€) | totalInvoiceValueEur (formatted) |
| Transport | modeOfTransportLabel |
| NOTc | natureOfTransactionCode |
| Terms | deliveryTerms |
| Txn Count | transactionCount (tooltip: "based on N raw transactions") |

- Rows grouped by Flow Type with a sticky group header row ("DISPATCHES" / "ARRIVALS") with subtotals.
- Grand total row at the bottom (bold): sum of Qty, Net Mass, Stat. Value, Invoice Value.
- Table is read-only (no edit actions).
- Rows striped (alternating background) for readability.

---

#### US-19 — No-Data State

**As a** user,
**I want** a clear message when no report data is available,
**so that** an empty table is not confusing.

**Acceptance Criteria**

- If `lines` is empty for the selected filters: show centred illustration + text "No transactions found for the selected filters."
- "Clear Filters" button resets flow type and entity to All.

---

## Data Model

```
legal_entities
──────────────
id          BIGSERIAL PK
name        VARCHAR NOT NULL
country_code CHAR(2) NOT NULL
vat_number  VARCHAR
active      BOOLEAN DEFAULT TRUE

eu_countries
────────────
code          CHAR(2) PK
name          VARCHAR NOT NULL
active_member BOOLEAN NOT NULL

parts
─────
id                BIGSERIAL PK
part_code         VARCHAR UNIQUE NOT NULL
name              VARCHAR NOT NULL
cn8_code          CHAR(8) NOT NULL
supplementary_unit VARCHAR(10)
unit_mass_kg      NUMERIC(8,2) NOT NULL
unit_price_eur    NUMERIC(10,2) NOT NULL

transactions
────────────
id                          BIGSERIAL PK
legal_entity_id             BIGINT FK → legal_entities
period                      DATE NOT NULL          -- first day of month
flow_type                   VARCHAR(10) NOT NULL   -- ARRIVAL | DISPATCH
counterpart_country_code    CHAR(2) FK → eu_countries
part_id                     BIGINT FK → parts
quantity                    INTEGER NOT NULL
net_mass_kg                 NUMERIC(10,2) NOT NULL
statistical_value_eur       NUMERIC(12,2) NOT NULL
invoice_value_eur           NUMERIC(12,2) NOT NULL
nature_of_transaction_code  SMALLINT NOT NULL
mode_of_transport           SMALLINT NOT NULL
delivery_terms              VARCHAR(5) NOT NULL
created_at                  TIMESTAMP DEFAULT NOW()
updated_at                  TIMESTAMP DEFAULT NOW()
```

---

## Docker Compose Architecture

```
┌─────────────────────────────────────────────────────┐
│  VPS                                                │
│                                                     │
│  ┌───────────┐    /api/**    ┌──────────────────┐  │
│  │ frontend  │──────────────▶│    backend       │  │
│  │ (Nginx)   │              │ (Spring Boot)     │  │
│  │ :80       │              │ :8080             │  │
│  └───────────┘              └────────┬─────────┘  │
│       ▲                              │ JDBC        │
│  Browser                    ┌────────▼─────────┐  │
│                              │    db             │  │
│                              │ (PostgreSQL 17)   │  │
│                              │ :5432             │  │
│                              └──────────────────┘  │
└─────────────────────────────────────────────────────┘
```

- Ports exposed to host: `80` (frontend only).
- `backend` and `db` are on an internal Docker network only.
- `.env` file supplies `POSTGRES_PASSWORD`, `POSTGRES_DB`, `POSTGRES_USER`.

---

## API Summary

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/health` | Health check |
| GET | `/api/transactions` | Paginated + filtered transaction list |
| GET | `/api/transactions/{id}` | Single transaction |
| PATCH | `/api/transactions/{id}` | Update editable fields |
| GET | `/api/legal-entities` | All legal entities |
| GET | `/api/countries` | Active EU member states |
| GET | `/api/parts` | All parts |
| GET | `/api/report` | Aggregated Intrastat report lines |
| GET | `/api/report/periods` | Available reporting periods |

---

## Test Cases

### API Tests

| ID | Endpoint | Scenario | Expected |
|----|----------|----------|----------|
| TC-01 | GET /api/transactions | No filters | 60 records, page 0 |
| TC-02 | GET /api/transactions?period=2025-01 | January filter | 20 records |
| TC-03 | GET /api/transactions?flowType=ARRIVAL | Arrival filter | ~24 records |
| TC-04 | PATCH /api/transactions/1 | Valid quantity change | 200, net_mass recalculated |
| TC-05 | PATCH /api/transactions/1 | quantity = 0 | 422, field error on quantity |
| TC-06 | PATCH /api/transactions/1 | counterpartCountryCode = "GB" | 422, not EU member |
| TC-07 | PATCH /api/transactions/1 | natureOfTransactionCode = 5 | 422, out of range |
| TC-08 | PATCH /api/transactions/9999 | Non-existent ID | 404 |
| TC-09 | GET /api/report?period=2025-01 | Valid period | summary totals correct, lines non-empty |
| TC-10 | GET /api/report?period=2025-01&flowType=DISPATCH | Flow filter | only DISPATCH lines returned |
| TC-11 | GET /api/report/periods | — | ["2025-03","2025-02","2025-01"] |
| TC-12 | GET /api/countries | — | 27 EU countries, no GB/NO |

### UI Smoke Tests

| ID | Page | Action | Expected |
|----|------|--------|----------|
| TC-13 | /transactions | Load page | Table shows 60 rows across pages; no console errors |
| TC-14 | /transactions | Filter by period = 2025-02 | Table shows 20 rows |
| TC-15 | /transactions | Filter by Flow = Dispatches | Only DISPATCH badges visible |
| TC-16 | /transactions | Click Edit on any row | Side panel opens with correct pre-filled values |
| TC-17 | /transactions | Edit panel: change quantity to 999, save | Row in table updates; net mass column changes |
| TC-18 | /transactions | Edit panel: change counterpart to France, cancel | Panel closes; original value unchanged |
| TC-19 | /transactions | Edit panel: submit quantity = -1 | Inline error "Must be greater than 0"; Save disabled |
| TC-20 | /report | Load page | Summary cards show non-zero values; lines table populated |
| TC-21 | /report | Change period to 2025-02 | Summary and lines update to February data |
| TC-22 | /report | Toggle flow to Arrivals | Only ARRIVAL rows visible; summary shows arrival-only totals |
| TC-23 | /report | Apply entity filter | Cards and lines reflect only that entity's transactions |

---

## Out of Scope

- Authentication / login
- Creating or deleting transactions
- Report export (CSV, XML, PDF)
- Multi-tenancy
- Live government portal submission
- Real-time data updates (WebSocket)
- Mobile-specific layout optimisation beyond Angular Material responsive breakpoints
