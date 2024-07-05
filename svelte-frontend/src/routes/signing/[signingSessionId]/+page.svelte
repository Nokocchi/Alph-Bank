<script lang="ts">
    import { goto } from "$app/navigation";
	import type { SigningStatusEnum, UpdateSigningStatusRequest } from "../../../types";
    import { SIGNING_SERVICE_URL } from "../../globals";
	import { SigningStatusEnumSchema } from "../../schema";
	import type { PageData } from "./$types";

	export let data: PageData;

	async function mockSigningResult(signingStatus: SigningStatusEnum) {
		const updateSigningStatusRequest: UpdateSigningStatusRequest = { newStatus: signingStatus};
		await fetch(SIGNING_SERVICE_URL + "/signing/" + data.signingSessionId, {
			method: "PATCH",
			headers: {
				Accept: "application/json",
				"Content-Type": "application/json",
			},
			body: JSON.stringify(updateSigningStatusRequest),
		});

		if(signingStatus === SigningStatusEnumSchema.Enum.COMPLETED){
			goto(data.onSuccessRedirectUrl);
		} else {
			goto(data.onFailRedirectUrl);
		}
	}
</script>

{data.documentToSign}
<button on:click={() => mockSigningResult(SigningStatusEnumSchema.Enum.COMPLETED)}>Mock success</button>
<button on:click={() => mockSigningResult(SigningStatusEnumSchema.Enum.CANCELLED)}>Mock cancelled</button>
<button on:click={() => mockSigningResult(SigningStatusEnumSchema.Enum.EXPIRED)}>Mock expired</button>
