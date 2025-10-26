<script lang="ts">
    import type { CorePaymentSearchResult } from "../../../../types";

    export let payments: CorePaymentSearchResult[];
    export let caption: string;
    export let currency: string;
</script>

<table>
    <caption>{caption}</caption>
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
                <td class="amount" class:positive={payment.remittanceAmount.amount > 0}
                    >{payment.remittanceAmount.amount} {payment.remittanceAmount.currency}</td
                >
            </tr>
        {/each}
    </tbody>
    <tfoot>
        <tr>
            <td colspan="4">Total</td>
            <td>{payments.reduce((partialSum, p) => partialSum + p.remittanceAmount.amount, 0)} {currency}</td>
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
        color: red;
    }

    td.amount.positive {
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
