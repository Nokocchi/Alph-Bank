<script lang="ts">
	import type { Account, CreateAccountRequest, Customer, UpdateCustomerRequest } from "../../../types";
	import type { PageData } from "./$types";
	import { goto } from "$app/navigation";
	import { ACCOUNT_SERVICE_URL, CUSTOMER_SERVICE_URL } from "../../globals";
	import { defaults, setError, setMessage, superForm, type Infer } from "sveltekit-superforms";
	import { CreateAccountRequestSchema, CreateCustomerSchema, UpdateCustomerRequestSchema } from "../../schema";
	import { zod } from "sveltekit-superforms/adapters";

	export let data: PageData;
	let newAccountName: string;
	let customer: Customer = data.customer;
	let accounts: Account[] = data.accountList.accounts;

	async function deleteCustomerBtnHandler() {
		await fetch(CUSTOMER_SERVICE_URL + "/customer/" + data.customer.id, {
			method: "DELETE",
			headers: {
				Accept: "application/json",
				"Content-Type": "application/json",
			},
		});
		goto("/customer/search");
	}

	async function createAccountBtnHandler() {
		let request: CreateAccountRequest = { customerId: customer.id, accountName: newAccountName, locale: customer.locale };

		const newAccountRes = await fetch(ACCOUNT_SERVICE_URL + "/account", {
			method: "POST",
			headers: {
				Accept: "application/json",
				"Content-Type": "application/json",
			},
			body: JSON.stringify(request),
		});
		let newAccount: Account = (await newAccountRes.json()) as Account;
		accounts.push(newAccount);
		accounts = accounts;
		newAccountName = "";
	}

	let updateCustomerRequest: UpdateCustomerRequest = {
		phoneNumber: customer.phoneNumber,
		firstName: customer.firstName,
		lastName: customer.lastName,
		address: customer.address,
	};

	const {
		form: updateCustomerForm,
		errors: updateCustomerErrors,
		message: updateCustomerMessage,
		enhance: updateCustomerEnhance,
	} = superForm(updateCustomerRequest, {
		SPA: true,
		dataType: "json",
		resetForm: false,
		clearOnSubmit: "errors-and-message",
		validators: zod(UpdateCustomerRequestSchema),
		async onUpdate({ form }) {
			if (!form.valid) return;
			try {
				const updatedCustomer: Customer = (await fetch(CUSTOMER_SERVICE_URL + "/customer/" + customer.id, {
					method: "PATCH",
					headers: {
						Accept: "application/json",
						"Content-Type": "application/json",
					},
					body: JSON.stringify($updateCustomerForm),
				}).then((response) => response.json())) as Customer;
				setMessage(form, "Successfully updated customer!");
				customer = updatedCustomer;
			} catch (e) {
				setError(form, "No can't do");
			}
		},
	});

	const defaultdata = defaults(zod(CreateAccountRequestSchema));

	const {
		form: createAccountForm,
		errors: createAccountErrors,
		message: createAccountMessage,
		enhance: createAccountEnhance,
	} = superForm(defaultdata, {
		SPA: true,
		dataType: "json",
		resetForm: true,
		clearOnSubmit: "errors-and-message",
		validators: zod(CreateAccountRequestSchema),
		onSubmit() {
			$createAccountForm.customerId = customer.id;
			$createAccountForm.locale = customer.locale;
		},
		async onUpdate({ form }) {
			if (!form.valid) return;
			try {
				const newAccount: Account = (await fetch(ACCOUNT_SERVICE_URL + "/account", {
					method: "POST",
					headers: {
						Accept: "application/json",
						"Content-Type": "application/json",
					},
					body: JSON.stringify($createAccountForm),
				}).then((response) => response.json())) as Account;
				setMessage(form, "Successfully updated customer!");
				accounts.push(newAccount);
				accounts = accounts;
			} catch (e) {
				setError(form, "No can't do");
			}
		},
	});
</script>

<div class="breadcrumb-bar">
	<a href="/">Home</a>
	- My profile
</div>

{#if $updateCustomerMessage}
	<div class="message">{$updateCustomerMessage}</div>
{/if}
<div class="column-form">
	<h2>Your profile</h2>
	<form method="POST" use:updateCustomerEnhance>
		<!--use:enhance?-->
		<label>
			First name
			<input bind:value={$updateCustomerForm.firstName} name="firstName" type="text" />
			{#if $updateCustomerErrors.firstName}<span class="invalid">{$updateCustomerErrors.firstName}</span>{/if}
		</label>
		<label>
			Last name
			<input bind:value={$updateCustomerForm.lastName} name="lastName" type="text" />
			{#if $updateCustomerErrors.lastName}<span class="invalid">{$updateCustomerErrors.lastName}</span>{/if}
		</label>
		<label>
			Street address
			<input bind:value={$updateCustomerForm.address.streetAddress} name="address.streetAddress" type="text" />
			{#if $updateCustomerErrors.address?.streetAddress}<span class="invalid">{$updateCustomerErrors.address?.streetAddress}</span
				>{/if}
		</label>
		<label>
			City
			<input bind:value={$updateCustomerForm.address.city} name="address.city" type="text" />
			{#if $updateCustomerErrors.address?.city}<span class="invalid">{$updateCustomerErrors.address?.city}</span>{/if}
		</label>
		<button>Update customer</button>
	</form>
</div>

<div class="column-form">
	<h2>Create account</h2>
	<form method="POST" use:createAccountEnhance>
		<label>
			Account name
			<input bind:value={$createAccountForm.accountName} name="accountName" type="text" />
			{#if $createAccountErrors.accountName}<span class="invalid">{$createAccountErrors.accountName}</span>{/if}
		</label>
		<button>Create account</button>
	</form>
</div>

<div class="account-list-panel">
	<h2>Your accounts</h2>
	{#each accounts as account}
		<a href="/customer/{customer.id}/account/{account.id}">
			{account.accountName}
			{account.balance.amount}
			{account.balance.currency}
		</a>
	{/each}
</div>

<button on:click={deleteCustomerBtnHandler}>Delete customer</button>

<style>

	.account-list-panel {
		margin: 40px 0;
		display: flex;
		flex-direction: column;
	}

	h2 {
		font-weight: bold;
	}

</style>
