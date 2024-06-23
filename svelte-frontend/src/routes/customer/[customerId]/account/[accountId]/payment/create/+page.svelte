<script lang="ts">
	import { goto } from "$app/navigation";
	import { defaults, superForm, type Infer } from "sveltekit-superforms";
	import type { PageData } from "./$types";
	import { zod } from "sveltekit-superforms/adapters";
	import { CreatePaymentSchema } from "../../../../../../schema";
	import type { Payment } from "../../../../../../../types";
	import { PAYMENT_SERVICE_URL } from "../../../../../../globals";

	export let data: PageData;

	const defaultdata = defaults(zod(CreatePaymentSchema));

	const { form, errors, message, enhance, delayed } = superForm<Infer<typeof CreatePaymentSchema>, { status: number; text: string }>(
		defaultdata,
		{
			SPA: true,
			dataType: "json",
			resetForm: true,
			clearOnSubmit: "errors-and-message",
			validators: zod(CreatePaymentSchema),
			onSubmit() {
				$form.fromAccountId = data.account.id;
				$form.fromCustomerId = data.customerId;
				$form.remittanceAmount.currency = data.account.balance.currency;
			}, async onUpdate({ form }) {
				if (!form.valid) return;
				try {
					const newPayment: Payment = (await fetch(PAYMENT_SERVICE_URL + "/payment", {
						method: "POST",
						headers: {
							Accept: "application/json",
							"Content-Type": "application/json",
						},
						body: JSON.stringify($form),
					}).then((response) => response.json())) as Payment;
					form.message = { status: 200, text: `${name}, brought to you by randomuser.me` };
				} catch (e) {
					form.message = { status: 500, text: `User not found.` };
				}
			},
		},
	);
</script>

<div class="breadcrumb-bar">
	<a href="/">Home</a>
	-
	<a href="/customer/{data.customerId}">My profile</a>
	-
	<a href="/customer/{data.customerId}/account/{data.account.id}">{data.account.accountName}</a>
	- Make payment
</div>

<div class="column-form">
	<form method="POST" use:enhance>
		<label>
			Recipient iban
			<input bind:value={$form.recipientIban} name="recipientIban" type="text" />
			{#if $errors.recipientIban}<span class="invalid">{$errors.recipientIban}</span>{/if}
		</label>

		<label>
			Amount
			<input bind:value={$form.remittanceAmount.amount} name="remittanceAmount" type="number" />
			{#if $errors.remittanceAmount?.amount}<span class="invalid">{$errors.remittanceAmount.amount}</span>{/if}
		</label>

		<label>
			Message to self
			<input bind:value={$form.messageToSelf} name="messageToSelf" type="text" />
			{#if $errors.messageToSelf}<span class="invalid">{$errors.messageToSelf}</span>{/if}
		</label>

		<label>
			Message to recipient
			<input bind:value={$form.messageToRecipient} name="messageToRecipient" type="text" />
			{#if $errors.messageToRecipient}<span class="invalid">{$errors.messageToRecipient}</span>{/if}
		</label>

		<label>
			Scheduled time
			<input bind:value={$form.scheduledDateTime} name="scheduledDateTime" type="datetime-local" />
			{#if $errors.scheduledDateTime}<span class="invalid">{$errors.scheduledDateTime}</span>{/if}
		</label>

		<button>Schedule payment</button>
	</form>
</div>
