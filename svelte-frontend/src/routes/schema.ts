import { z } from 'zod';

export const LocaleSchema = z.string().min(5);

export const CustomerIdSchema = z.string().uuid();
export const AccountIdSchema = z.string().uuid();
export const PaymentIdSchema = z.string().uuid();
export const LoanIdSchema = z.string().uuid();
export const GovernmentIdSchema = z.string().min(1);

export const AddressSchema = z.object({
	streetAddress: z.string().min(1),
	city: z.string().min(1),
	country: z.string().min(1)
})

// Define at the top-level so it stays in memory and the adapter can be cached
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

export const PaymentSchema = z.object({
	paymentId: PaymentIdSchema,
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

export const PaymentSearchResultSchema = z.object({
	paymentId: PaymentIdSchema,
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
	remittanceAmount: MonetaryAmountSchema,
	messageToSelf: z.string(),
	messageToRecipient: z.string(),
	scheduledDateTime: z.string()
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

export const CreateLoanRequestSchema = z.object({
	customerId: CustomerIdSchema,
	accountId: AccountIdSchema,
	principal: MonetaryAmountSchema,
	fixedRateInterestAPR: z.number(),
	loanTermMonths: z.number().int(),
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