<script lang="ts">
	import { goto } from "$app/navigation";
	import type { Account, PaymentSearchResult } from "../../../../../types";
	import { ACCOUNT_SERVICE_URL } from "../../../../globals";
	import type { PageData } from "./$types";

	export let data: PageData;
	let account: Account = data.account;
	let payments: PaymentSearchResult[] = data.payments;

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

<a href="/customer/{data.customerId}/account/{account.id}/payment/create">Make payment</a>

Balance: {account.balance.amount} {account.balance.currency}
<br />
Iban: {account.iban}

{#if account.balance.amount == 0 && payments.length == 0}
	<button on:click={deleteAccountBtnHandler}>Delete account</button>
{/if}
<table>
	<caption> Account transactions </caption>
	<thead>
		<tr>
			<th scope="col">Recipient IBAN</th>
			<th scope="col">Executed?</th>
			<th scope="col">Message</th>
			<th scope="col">Date & Time</th>
			<th scope="col">Amount</th>
		</tr>
	</thead>
	<tbody>
		{#each payments as payment}
			<tr>
				<td>{payment.recipientIban}</td>
				<td>{payment.executed}</td>
				<td>{payment.message}</td>
				<td>{payment.visibleOnAccountDateTime}</td>
				<td>{payment.remittanceAmount.amount} {payment.remittanceAmount.currency}</td>
			</tr>
		{/each}
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4">Total</td>
			<td>{payments.reduce((partialSum, p) => partialSum + p.remittanceAmount.amount, 0)} {account.balance.currency}</td>
		</tr>
	</tfoot>
</table>

<style>
	table {
		border-collapse: collapse;
		border: 2px solid rgb(140 140 140);
		font-family: sans-serif;
		font-size: 0.8rem;
		letter-spacing: 1px;
	}

	caption {
		caption-side: top;
		padding: 10px;
		font-weight: bold;
	}

	thead,
	tfoot {
		background-color: rgb(228 240 245);
	}

	th,
	td {
		border: 1px solid rgb(160 160 160);
		padding: 8px 10px;
	}

	td:last-of-type {
		text-align: center;
	}

	tbody > tr:nth-of-type(even) {
		background-color: rgb(237 238 242);
	}

	tfoot th {
		text-align: right;
	}

	tfoot td {
		font-weight: bold;
	}
</style>
