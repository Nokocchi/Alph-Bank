<script lang="ts">

	import type { PageData } from "./$types";
    import type { Loan, LoanApplication } from "../../../../types";
    import LoanList from "./LoanList.svelte";

	export let data: PageData;
	let approvedLoans: Loan[] = data.approvedLoans;
	let loanApplications: LoanApplication[] = data.loanApplications; 
	
</script>

<LoanList {loanApplications} {approvedLoans} currency={loanApplications[0].principal.currency}/>