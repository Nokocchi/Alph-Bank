<script lang="ts">
	import type { Customer } from "../../../types";
    import { COUNTRIES } from "../../globals";
	import { CreateCustomerSchema } from "../../schema";
	import { languageStore } from "../../stores";
	import type { PageData } from "./$types";
	import { type Infer, superForm, defaults } from "sveltekit-superforms";
	import { zod } from "sveltekit-superforms/adapters";

	export let data: PageData;
	let customerList: Customer[] = data.customers;

	const defaultdata = defaults(zod(CreateCustomerSchema));

	const { form, errors, message, enhance, delayed } = superForm<Infer<typeof CreateCustomerSchema>, { status: number; text: string }>(
		defaultdata,
		{
			SPA: true,
			dataType: "json",
			resetForm: true,
			clearOnSubmit: "errors-and-message",
			validators: zod(CreateCustomerSchema),
			onSubmit() {
				$form.locale = $languageStore + "_" + $form.address.country;
			},
			async onUpdate({ form }) {
				if (!form.valid) return;
				try {
					const newCustomer: Customer = (await fetch("http://localhost:8083/customer", {
						method: "POST",
						headers: {
							Accept: "application/json",
							"Content-Type": "application/json",
						},
						body: JSON.stringify($form),
					}).then((response) => response.json())) as Customer;
					customerList.push(newCustomer);
					customerList = customerList;
					form.message = { status: 200, text: `${name}, brought to you by randomuser.me` };
				} catch (e) {
					form.message = { status: 500, text: `User not found.` };
				}
			},
		},
	);
</script>

<div class="column-form">
	<form method="POST" use:enhance>
		<!--use:enhance?-->
		<label>
			First name
			<input bind:value={$form.firstName} name="firstName" type="text" />
			{#if $errors.firstName}<span class="invalid">{$errors.firstName}</span>{/if}
		</label>
		<label>
			Last name
			<input bind:value={$form.lastName} name="lastName" type="text" />
			{#if $errors.lastName}<span class="invalid">{$errors.lastName}</span>{/if}
		</label>
		<label>
			Street address
			<input bind:value={$form.address.streetAddress} name="address.streetAddress" type="text" />
			{#if $errors.address?.streetAddress}<span class="invalid">{$errors.address?.streetAddress}</span>{/if}
		</label>
		<label>
			City
			<input bind:value={$form.address.city} name="address.city" type="text" />
			{#if $errors.address?.city}<span class="invalid">{$errors.address?.city}</span>{/if}
		</label>
		<label>
			Country
			<select bind:value={$form.address.country}>
				{#each COUNTRIES as country}
					<option value={country.countryCode}>{country.asText}</option>
				{/each}
			</select>
			{#if $errors.address?.country}<span class="invalid">{$errors.address?.country}</span>{/if}
		</label>
		<label>
			Government id
			<input bind:value={$form.governmentId} name="governmentId" type="text" />
			{#if $errors.governmentId}<span class="invalid">{$errors.governmentId}</span>{/if}
		</label>
		<button>Create customer</button>
	</form>
</div>

{#each customerList as customer}
	<a href="/customer/{customer.id}"
		>Log in to customer {customer.firstName} {customer.lastName} - {customer.locale}:{customer.governmentId}</a
	>
{/each}
