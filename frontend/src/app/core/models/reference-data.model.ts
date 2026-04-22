export interface LegalEntity {
  id: number;
  name: string;
  countryCode: string;
}

export interface Country {
  code: string;
  name: string;
}

export interface Part {
  id: number;
  partCode: string;
  name: string;
  cn8Code: string;
}
