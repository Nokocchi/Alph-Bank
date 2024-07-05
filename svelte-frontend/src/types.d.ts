import { z } from 'zod';
import type { CreateAccountSchema, CustomerSchema, PaymentSchema, PaymentSearchResultSchema, CreateCustomerSchema, UpdateCustomerRequestSchema, LoanSchema, SigningSessionSchema, SigningStatusEnumSchema, UpdateSigningStatusRequestSchema, CreateLoanApplicationResponseSchema, SearchLoanApplicationsResponseSchema, SearchLoansResponseSchema, LoanApplicationSchema } from './routes/schema';

type Address = {
    streetAddress: string,
    city: string,
    country: string
}

type Customer = z.infer<typeof CustomerSchema>;

type CustomerList = {
    customers: Customer[]
}

type MonetaryAmount = {
    amount: number,
    currency: string
}

type Account = {
    id: string,
    accountName: string,
    balance: MonetaryAmount,
    iban: string
}

type AccountList = {
    accounts: Account[]
}

type CreateAccountRequest = z.infer<typeof CreateAccountSchema>;

type Payment = z.infer<typeof PaymentSchema>;

type PaymentSearchResult = z.infer<typeof PaymentSearchResultSchema>;

type PaymentSearchRestResponse = {
    payments: PaymentSearchResult[];
}

type CountryCodeAndCountry = {
    countryCode: string,
    asText: string
}

type UpdateCustomerRequest = z.infer<typeof UpdateCustomerRequestSchema>;

type SigningSession = z.infer<typeof SigningSessionSchema>;

type SigningStatusEnum = z.infer<typeof SigningStatusEnumSchema>;

type UpdateSigningStatusRequest = z.infer<typeof UpdateSigningStatusRequestSchema>;

type CreateLoanApplicationResponse = z.infer<typeof CreateLoanApplicationResponseSchema>;

type SearchLoanApplicationsResponse = z.infer<typeof SearchLoanApplicationsResponseSchema>;
type SearchLoansResponse = z.infer<typeof SearchLoansResponseSchema>;
type Loan = z.infer<typeof LoanSchema>;
type LoanApplication = z.infer<typeof LoanApplicationSchema>;