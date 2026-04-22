export interface LegalEntitySummary {
  id: number;
  name: string;
  countryCode: string;
}

export interface PartSummary {
  id: number;
  partCode: string;
  name: string;
  cn8Code: string;
}

export interface Transaction {
  id: number;
  legalEntity: LegalEntitySummary;
  period: string;
  flowType: 'ARRIVAL' | 'DISPATCH';
  counterpartCountryCode: string;
  counterpartCountryName: string;
  part: PartSummary;
  quantity: number;
  netMassKg: number;
  statisticalValueEur: number;
  invoiceValueEur: number;
  natureOfTransactionCode: number;
  modeOfTransport: number;
  deliveryTerms: string;
  createdAt: string;
  updatedAt: string;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface TransactionPatch {
  quantity?: number;
  statisticalValueEur?: number;
  invoiceValueEur?: number;
  counterpartCountryCode?: string;
  modeOfTransport?: number;
  natureOfTransactionCode?: number;
  deliveryTerms?: string;
}

export interface ValidationErrorResponse {
  errors: Array<{ field: string; message: string }>;
}
