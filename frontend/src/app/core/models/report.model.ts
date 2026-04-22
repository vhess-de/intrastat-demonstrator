export interface ReportSummary {
  totalLines: number;
  totalStatisticalValueEur: number;
  totalNetMassKg: number;
  totalQuantity: number;
  arrivalLines: number;
  dispatchLines: number;
  arrivalValueEur: number;
  dispatchValueEur: number;
}

export interface ReportLine {
  legalEntityId: number;
  legalEntityName: string;
  legalEntityCountry: string;
  flowType: 'ARRIVAL' | 'DISPATCH';
  counterpartCountryCode: string;
  counterpartCountryName: string;
  cn8Code: string;
  partName: string;
  totalQuantity: number;
  totalNetMassKg: number;
  totalStatisticalValueEur: number;
  totalInvoiceValueEur: number;
  modeOfTransport: number;
  modeOfTransportLabel: string;
  natureOfTransactionCode: number;
  deliveryTerms: string;
  transactionCount: number;
}

export interface ReportResponse {
  period: string;
  generatedAt: string;
  summary: ReportSummary;
  lines: ReportLine[];
}

// ── Virtual rows injected into the table data source ─────────────────────────

export interface GroupHeaderRow {
  rowType: 'GROUP_HEADER';
  flowType: 'ARRIVAL' | 'DISPATCH';
  lineCount: number;
  subtotalQty: number;
  subtotalNetMassKg: number;
  subtotalStatValueEur: number;
  subtotalInvValueEur: number;
  subtotalTxnCount: number;
}

export interface SkeletonRow {
  rowType: 'SKELETON';
}

export type TableRow = ReportLine | GroupHeaderRow | SkeletonRow;
