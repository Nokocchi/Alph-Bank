import { z } from 'zod';
import { LOAN_APPLICATION_STATUSES, SIGNING_STATUSES } from './globals';

// BASICS

export const uuidSchema = z.string().uuid();
export const LocaleSchema = z.string().min(5);
export const GovernmentIdSchema = z.string().min(1);
export const CustomerIdSchema = uuidSchema;
export const AccountIdSchema = uuidSchema;
export const CorePaymentIdSchema = uuidSchema;
export const PaymentIdSchema = uuidSchema;
export const LoanIdSchema = uuidSchema;
export const LoanApplicationIdSchema = uuidSchema;
export const SigningSessionIdSchema = uuidSchema;
export const BasketIdSchema = uuidSchema;

export const AddressSchema = z.object({
	streetAddress: z.string().min(1),
	city: z.string().min(1),
	country: z.string().min(1)
})

export const CreateCustomerSchema = z.object({
	locale: LocaleSchema,
	governmentId: GovernmentIdSchema,
	phoneNumber: z.string().optional(),
	firstName: z.string().min(1),
	lastName: z.string().min(1),
	address: AddressSchema
});

export const CustomerSchema = z.object({
	id: CustomerIdSchema,
	governmentId: GovernmentIdSchema,
	phoneNumber: z.string().min(1).optional(),
	locale: LocaleSchema,
	firstName: z.string().min(1),
	lastName: z.string().min(1),
	address: AddressSchema
});

export const CreateAccountSchema = z.object({
	customerId: CustomerIdSchema,
	accountName: z.string(),
	locale: LocaleSchema
})

export const MonetaryAmountSchema = z.object({
	amount: z.number(),
	currency: z.string().min(3).max(3)
})


export const UpdateCustomerRequestSchema = z.object({
	address: AddressSchema,
	phoneNumber: z.string().optional(),
	firstName: z.string().min(1),
	lastName: z.string().min(1)
})

export const CreateAccountRequestSchema = z.object({
	customerId: CustomerIdSchema,
	locale: LocaleSchema,
	accountName: z.string().min(1)
})

export const CreateLoanApplicationRequestSchema = z.object({
	customerId: CustomerIdSchema,
	accountId: AccountIdSchema,
	governmentId: GovernmentIdSchema,
	locale: LocaleSchema,
	principal: MonetaryAmountSchema,
	fixedRateInterestAPR: z.number(),
	loanTermMonths: z.number().int(),
	onSigningSuccessRedirectUrl: z.string().min(1),
	onSigningFailedRedirectUrl: z.string().min(1)
})

export const CreateLoanApplicationResponseSchema = z.object({
	signingUrl: z.string().min(1)
})

export const SigningStatusEnumSchema = z.enum(SIGNING_STATUSES);
export const LoanApplicationStatusEnumSchema = z.enum(LOAN_APPLICATION_STATUSES);

export const UpdateSigningStatusRequestSchema = z.object({
	newStatus: SigningSessionIdSchema
})


export const LoanSchema = z.object({
	loanId: LoanIdSchema,
	customerId: CustomerIdSchema,
	accountId: AccountIdSchema,
	principal: MonetaryAmountSchema,
	fixedRateInterestAPR: z.number(),
	loanTermMonths: z.number().int(),
	payoutDateTime: z.date()
})

export const LoanApplicationSchema = z.object({
	loanApplicationId: LoanIdSchema,
	customerId: CustomerIdSchema,
	accountId: AccountIdSchema,
	locale: LocaleSchema,
	governmentId: GovernmentIdSchema,
	principal: MonetaryAmountSchema,
	fixedRateInterestAPR: z.number(),
	loanTermMonths: z.number().int(),
	applicationStatus: LoanApplicationStatusEnumSchema
})

export const SearchLoanApplicationsResponseSchema = z.object({
	loanApplications: z.array(LoanApplicationSchema)
});

export const SearchLoansResponseSchema = z.object({
	loans: z.array(LoanSchema)
});

export const SigningSessionSchema = z.object({
	signingSessionId: SigningSessionIdSchema,
	customerId: CustomerIdSchema,
	governmentId: GovernmentIdSchema,
	locale: LocaleSchema,
	signingStatus: z.string(),
	documentToSign: z.string(),
	signingStatusUpdatedRoutingKey: z.string(),
	onSuccessRedirectUrl: z.string(),
	onFailRedirectUrl: z.string(),
	signingUrl: z.string()
})


export const CorePaymentSchema = z.object({
	paymentId: CorePaymentIdSchema,
	fromCustomerId: CustomerIdSchema,
	fromAccountId: AccountIdSchema,
	executed: z.boolean(),
	remittanceAmount: MonetaryAmountSchema,
	recipientIban: z.string().min(1),
	recipientAccountId: z.string().uuid(),
	messageToSelf: z.string(),
	messageToRecipient: z.string(),
	executionDateTime: z.date(),
	scheduledDateTime: z.date()
})

export const CorePaymentSearchResultSchema = z.object({
	paymentId: CorePaymentIdSchema,
	fromCustomerId: CustomerIdSchema,
	fromAccountId: AccountIdSchema,
	executed: z.boolean(),
	remittanceAmount: MonetaryAmountSchema,
	recipientIban: z.string().min(1),
	recipientAccountId: z.string().uuid(),
	message: z.string(),
	visibleOnAccountDateTime: z.date()
})

export const CreatePaymentSchema = z.object({
	fromCustomerId: CustomerIdSchema,
	fromAccountId: AccountIdSchema,
	recipientIban: z.string().min(1),
	paymentAmount: MonetaryAmountSchema,
	messageToSelf: z.string(),
	messageToRecipient: z.string(),
	scheduledDateTime: z.string()
})

export const PaymentSchema = z.object({
	paymentId: PaymentIdSchema,
	fromAccountId: AccountIdSchema,
	basketId: BasketIdSchema,
	paymentAmount: MonetaryAmountSchema,
	recipientIban: z.string().min(1),
	messageToSelf: z.string(),
	messageToRecipient: z.string(),
	scheduledDateTime: z.date()
})

export const BasketSchema = z.object({
	basketId: BasketIdSchema,
	payments: z.array(PaymentSchema)
})

export const AuthorizePaymentBasketSchema = z.object({
	customerId: CustomerIdSchema,
	governmentId: GovernmentIdSchema,
	locale: LocaleSchema,
	onSigningSuccessRedirectUrl: z.string().min(1),
	onSigningFailedRedirectUrl: z.string().min(1)
})

export const AuthorizePaymentBasketResponseSchema = z.object({
	signingUrl: z.string().min(1)
})