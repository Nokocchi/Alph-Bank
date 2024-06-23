import { z } from 'zod';

export const LocaleSchema = z.string().min(5);

export const AddressSchema = z.object({
	streetAddress: z.string().min(1),
	city: z.string().min(1),
	country: z.string().min(1)
})

// Define at the top-level so it stays in memory and the adapter can be cached
export const CreateCustomerSchema = z.object({
	locale: LocaleSchema,
	governmentId: z.string().min(1),
	phoneNumber: z.string().optional(),
	firstName: z.string().min(1),
	lastName: z.string().min(1),
	address: AddressSchema
});

export const CustomerSchema = z.object({
    id: z.string().uuid(),
    governmentId: z.string().min(1),
    phoneNumber: z.string().min(1).optional(),
    locale: LocaleSchema,
    firstName: z.string().min(1),
    lastName: z.string().min(1),
    address: AddressSchema
});

export const CreateAccountSchema = z.object({
	customerId: z.string().uuid(),
	accountName: z.string(),
	locale: LocaleSchema
})

export const MonetaryAmount = z.object({
	amount: z.number().gt(0),
	currency: z.string().min(3).max(3)
})

export const PaymentSchema = z.object({
	paymentId: z.string().uuid(),
	fromCustomerId: z.string().uuid(),
	fromAccountId: z.string().uuid(),
	executed: z.boolean(),
	remittanceAmount: MonetaryAmount,
	recipientIban: z.string().min(1),
	recipientAccountId: z.string().uuid(),
	messageToSelf: z.string(),
	messageToRecipient: z.string(),
	executionDateTime: z.date(),
	scheduledDateTime: z.date()
})

export const PaymentSearchResultSchema = z.object({
	paymentId: z.string().uuid(),
	fromCustomerId: z.string().uuid(),
	fromAccountId: z.string().uuid(),
	executed: z.boolean(),
	remittanceAmount: MonetaryAmount,
	recipientIban: z.string().min(1),
	recipientAccountId: z.string().uuid(),
	message: z.string(),
	visibleOnAccountDateTime: z.date()
})

export const CreatePaymentSchema = z.object({
	fromCustomerId: z.string().uuid(),
	fromAccountId: z.string().uuid(),
	recipientIban: z.string().min(1),
	remittanceAmount: MonetaryAmount,
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
	customerId: z.string().uuid(),
	locale: LocaleSchema,
	accountName: z.string().min(1)
})