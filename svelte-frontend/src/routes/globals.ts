import type { CountryCodeAndCountry } from "../types";

export const ACCOUNT_SERVICE_URL: string = "http://localhost:8080";
export const CORE_PAYMENT_SERVICE_URL: string = "http://localhost:8080";
export const LOAN_SERVICE_URL: string = "http://localhost:8080";
export const CUSTOMER_SERVICE_URL: string = "http://localhost:8080";
export const SIGNING_SERVICE_URL: string = "http://localhost:8085";
export const LOAN_APPLICATION_SERVICE_URL: string = "http://localhost:8086";
export const PAYMENT_SERVICE_URL: string = "http://localhost:8087";


export const COUNTRIES: CountryCodeAndCountry[] = [
    { countryCode: "DK", asText: "Danmark" },
    { countryCode: "NO", asText: "Norge" },
    { countryCode: "SE", asText: "Sverige" },
    { countryCode: "FI", asText: "Suomi" },
];

export const SIGNING_STATUSES = ["CREATED", "IN_PROGRESS", "COMPLETED", "CANCELLED", "EXPIRED"] as const;
export const LOAN_APPLICATION_STATUSES = ["CREATED", "SIGNING_STARTED", "SIGNING_FAILED", "CREDIT_CHECK_PENDING", "MANUAL_INSPECTION", "APPROVED"] as const;
