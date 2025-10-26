<script lang="ts">
	import { defaults, setError, setMessage, superForm, type Infer } from "sveltekit-superforms";
	import { zod } from "sveltekit-superforms/adapters";
	import { CreatePaymentSchema } from "../../../../../schema";
	import type { CorePayment, Payment } from "../../../../../../types";
	import { PAYMENT_SERVICE_URL } from "../../../../../globals";
	import type { PageData } from "../[[accountId]]/$types";
    import TransactionList from "../../../account/TransactionList.svelte";
    import PaymentBasket from "../../../account/PaymentBasket.svelte";

	export let data: PageData;
	// $form.fromAccountId is bound to the account selection dropdown, so selectedAccount will always be the account selected in that dropdown and vice vers
	// $form.fromAccountId is set to be equal to the accountId this page was initialized with. If that's undefined, it defaults to first account in the list
	$: selectedAccount = data.accounts.find((account) => account.id === $form.fromAccountId)

	const defaultdata = defaults(zod(CreatePaymentSchema));

	const { form, errors, message, enhance, delayed } = superForm(defaultdata, {
		SPA: true,
		dataType: "json",
		resetForm: true,
		clearOnSubmit: "errors-and-message",
		validators: zod(CreatePaymentSchema),
		onSubmit() {
			$form.fromCustomerId = data.customerId;
			if (!selectedAccount) {
					throw new TypeError("Account should always be present");
				}
			$form.paymentAmount.currency = selectedAccount.balance.currency;
		},
		async onUpdate({ form }) {
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
				setMessage(form, "Payment created");
			} catch (e) {
				setError(form, "Error creating payment");
			}
		},
	});

	$form.fromAccountId = data.selectedAccountId;
</script>

{#key selectedAccount}
	<div class="breadcrumb-bar">
		<a href="/">Home</a>
		-
		<a href="/customer/{data.customerId}">My profile</a>
		{#if selectedAccount}
			-
			<a href="/customer/{data.customerId}/account/{selectedAccount.id}">{selectedAccount.accountName}</a>
		{/if}
		- Make payment
	</div>
{/key}

{#if data.currentBasket}
<PaymentBasket basketId={data.currentBasket.basketId} customer={data.customer} caption="soup" payments={data.currentBasket.payments}/>
{/if}

<div class="column-form">
	<label>
		From account
		<select bind:value={$form.fromAccountId}>
			{#each data.accounts as account}
				<option value={account.id}>{account.accountName} - {account.balance.amount} {account.balance.currency}</option>
			{/each}
		</select>
	</label>

	<form method="POST" use:enhance>
		<label>
			Recipient iban
			<input bind:value={$form.recipientIban} name="recipientIban" type="text" />
			{#if $errors.recipientIban}<span class="invalid">{$errors.recipientIban}</span>{/if}
		</label>

		<label>
			Amount
			<input bind:value={$form.paymentAmount.amount} name="remittanceAmount" type="number" />
			{#if $errors.paymentAmount?.amount}<span class="invalid">{$errors.paymentAmount.amount}</span>{/if}
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
