import type { CountryCodeAndCountry } from "../types";

export const ACCOUNT_SERVICE_URL: string = "http://localhost:8080";
export const CUSTOMER_SERVICE_URL: string = "http://localhost:8083";
export const PAYMENT_SERVICE_URL: string = "http://localhost:8081";

export const COUNTRIES: CountryCodeAndCountry[] = [
    { countryCode: "DK", asText: "Danmark" },
    { countryCode: "NO", asText: "Norge" },
    { countryCode: "SE", asText: "Sverige" },
    { countryCode: "FI", asText: "Suomi" },
];
