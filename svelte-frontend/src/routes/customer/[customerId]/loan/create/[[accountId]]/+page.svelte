<script lang="ts">
	import { defaults, setError, setMessage, superForm } from "sveltekit-superforms";
	import { zod } from "sveltekit-superforms/adapters";
	import { CreateLoanRequestSchema } from "../../../../../schema";
	import type { Loan } from "../../../../../../types";
	import { LOAN_SERVICE_URL } from "../../../../../globals";
	import type { PageData } from "../[[accountId]]/$types";

	export let data: PageData;
	// $form.fromAccountId is bound to the account selection dropdown, so selectedAccount will always be the account selected in that dropdown and vice vers
	// $form.fromAccountId is set to be equal to the accountId this page was initialized with. If that's undefined, it defaults to first account in the list
	$: selectedAccount = data.accounts.find((account) => account.id === $form.accountId)
	$: totalLoanAmount = calculateTotal($form.principal.amount, $form.fixedRateInterestAPR, $form.loanTermMonths);

	const calculateTotal = (principal: number, fixedRateInterestAPR: number, loanTermMonths: number): number => {
		const monthlyInterestRate: number = fixedRateInterestAPR/12/100;
		const percentOverEntireLoanTerm: number = (1 + monthlyInterestRate)**loanTermMonths;
		const monthlyInstallment: number = principal * ((monthlyInterestRate * percentOverEntireLoanTerm) / (percentOverEntireLoanTerm-1))
		return monthlyInstallment * loanTermMonths;
	}	


	const defaultdata = defaults(zod(CreateLoanRequestSchema));

	const { form, errors, message, enhance, delayed } = superForm(defaultdata, {
		SPA: true,
		dataType: "json",
		resetForm: true,
		clearOnSubmit: "errors-and-message",
		validators: zod(CreateLoanRequestSchema),
		onSubmit() {
			$form.customerId = data.customerId;
			if (!selectedAccount) {
					throw new TypeError("Account should always be present");
				}
			$form.principal.currency = selectedAccount.balance.currency;
		},
		async onUpdate({ form }) {
			if (!form.valid) return;
			try {
				const newLoan: Loan = (await fetch(LOAN_SERVICE_URL + "/loan", {
					method: "POST",
					headers: {
						Accept: "application/json",
						"Content-Type": "application/json",
					},
					body: JSON.stringify($form),
				}).then((response) => response.json())) as Loan;
				setMessage(form, "Applied for loan!");
			} catch (e) {
				setError(form, "Error applying for loan");
			}
		},
	});

	$form.accountId = data.selectedAccountId;
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
		- Apply for loan
	</div>
{/key}

<div class="column-form">
	<label>
		From account
		<select bind:value={$form.accountId}>
			{#each data.accounts as account}
				<option value={account.id}>{account.accountName} - {account.balance.amount} {account.balance.currency}</option>
			{/each}
		</select>
	</label>

	<form method="POST" use:enhance>
		<label>
			Yearly Interest rate
			<input bind:value={$form.fixedRateInterestAPR} name="fixedRateInterestAPR" type="number" />
			{#if $errors.fixedRateInterestAPR}<span class="invalid">{$errors.fixedRateInterestAPR}</span>{/if}
		</label>

		<label>
			Loan Amount
			<input bind:value={$form.principal.amount} name="principal" type="number" />
			{#if $errors.principal?.amount}<span class="invalid">{$errors.principal?.amount}</span>{/if}
		</label>

		<label>
			Loan term (Monthly installments)
			<input bind:value={$form.loanTermMonths} name="loanTermMonths" type="number" />
			{#if $errors.loanTermMonths}<span class="invalid">{$errors.loanTermMonths}</span>{/if}
		</label>

		<button>Apply</button>
	</form>

	<span>Total: {totalLoanAmount}</span>
	<br />
	<span>Loan cost: {totalLoanAmount - $form.principal.amount}</span>
</div>
