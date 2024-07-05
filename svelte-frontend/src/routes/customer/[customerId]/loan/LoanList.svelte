<script lang="ts">
    import type { Loan, LoanApplication } from "../../../../types";
    import { LoanApplicationSchema, LoanApplicationStatusEnumSchema } from "../../../schema";

    export let loanApplications: LoanApplication[];
    export let approvedLoans: Loan[];
    export let currency: string;
</script>

<table>
    <caption>Approved loans</caption>
    <thead>
        <tr>
            <th scope="col">Annual fixed interest rate</th>
            <th scope="col">Loan terms (months)</th>
            <th scope="col">Paid out</th>
            <th scope="col">Amount</th>
        </tr>
    </thead>
    <tbody>
        {#each approvedLoans as loan}
            <tr>
                <td>{loan.fixedRateInterestAPR}</td>
                <td>{loan.loanTermMonths}</td>
                <td>{loan.payoutDateTime}</td>
                <td class="amount">{loan.principal.amount} {loan.principal.currency}</td>
            </tr>
        {/each}
    </tbody>
    <tfoot>
        <tr>
            <td colspan="4">Total</td>
            <td>{approvedLoans.reduce((partialSum, p) => partialSum + p.principal.amount, 0)} {currency}</td>
        </tr>
    </tfoot>
</table>

<table>
    <caption>All loan applications</caption>
    <thead>
        <tr>
            <th scope="col">Annual fixed interest rate</th>
            <th scope="col">Loan terms (months)</th>
            <th scope="col">Application status</th>
            <th scope="col">Amount</th>
        </tr>
    </thead>
    <tbody>
        {#each loanApplications as loanApplication}
            <tr>
                <td>{loanApplication.fixedRateInterestAPR}</td>
                <td>{loanApplication.loanTermMonths}</td>
                <td
                    class:signing-started={loanApplication.applicationStatus === LoanApplicationStatusEnumSchema.Enum.SIGNING_STARTED}
                    class:approved={loanApplication.applicationStatus === LoanApplicationStatusEnumSchema.Enum.APPROVED}
                    class:signing-failed={loanApplication.applicationStatus === LoanApplicationStatusEnumSchema.Enum.SIGNING_FAILED}
                    >{loanApplication.applicationStatus}</td
                >
                <td class="amount">{loanApplication.principal.amount} {loanApplication.principal.currency}</td>
            </tr>
        {/each}
    </tbody>
    <tfoot>
        <tr>
            <td colspan="4">Total</td>
            <td>{loanApplications.reduce((partialSum, p) => partialSum + p.principal.amount, 0)} {currency}</td>
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

    td.amount {
        font-weight: bold;
    }

    td.signing-started {
        color: yellow;
    }

    td.signing-failed {
        color: red;
    }
    

    td.approved {
        color: green;
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
