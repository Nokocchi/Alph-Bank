<script lang="ts">
	import { goto } from "$app/navigation";
	import type { Account, PaymentSearchResult } from "../../../../../types";
	import { ACCOUNT_SERVICE_URL } from "../../../../globals";
	import TransactionList from "../TransactionList.svelte";
	import type { PageData } from "./$types";

	export let data: PageData;
	let account: Account = data.account;
	let payments: PaymentSearchResult[] = data.payments;

	let notYetExecutedPayments: PaymentSearchResult[] = [];
	let executedPayments: PaymentSearchResult[] = [];

	payments.forEach((payment) => {
		if (payment.executed) {
			executedPayments.push(payment);
		} else {
			notYetExecutedPayments.push(payment);
		}
	});

	async function deleteAccountBtnHandler() {
		await fetch(ACCOUNT_SERVICE_URL + "/account/" + data.account.id, {
			method: "DELETE",
			headers: {
				Accept: "application/json",
				"Content-Type": "application/json",
			},
		});
		goto("/customer/" + data.customerId);
	}
</script>

<div class="breadcrumb-bar">
	<a href="/">Home</a>
	-
	<a href="/customer/{data.customerId}">My profile</a>
	-
	{account.accountName}
</div>

<a href="/customer/{data.customerId}/payment/create/{account.id}">Make payment</a>
<a href="/customer/{data.customerId}/loan/create/{account.id}">Apply for loan</a>

Balance: {account.balance.amount}
{account.balance.currency}
<br />
Iban: {account.iban}

{#if account.balance.amount == 0 && payments.length == 0}
	<button on:click={deleteAccountBtnHandler}>Delete account</button>
{/if}

{#if notYetExecutedPayments}
	<TransactionList payments={notYetExecutedPayments} caption="Upcoming payments" currency={account.balance.currency} />
{/if}
<TransactionList payments={executedPayments} caption="Executed payments" currency={account.balance.currency} />


