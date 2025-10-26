<script lang="ts">
    import { goto } from "$app/navigation";
    import type { AuthorizePaymentBasket, AuthorizePaymentBasketResponse, Customer, Payment } from "../../../../types";
    import { PAYMENT_SERVICE_URL } from "../../../globals";

    export let basketId: string;
    export let payments: Payment[];
    export let caption: string;
    export let customer: Customer;
    let currency: string = payments[0].paymentAmount.currency;

    async function signAndExecuteBtnHandler() {

        let authorizeRequest: AuthorizePaymentBasket = {
            customerId: customer.id,
            nationalId: customer.nationalId,
            locale: customer.locale,
            onSigningSuccessRedirectUrl: "wow",
            onSigningFailedRedirectUrl: "not wow"
        }

        const signingUrlResponse = await fetch(PAYMENT_SERVICE_URL + "/basket/" + basketId + "/authorize", {
            method: "POST",
            headers: {
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(authorizeRequest),
        });
        const signingUrlObject = (await signingUrlResponse.json()) as AuthorizePaymentBasketResponse;
        goto(signingUrlObject.signingUrl);
    }
</script>

<button on:click={signAndExecuteBtnHandler}>Sign and execute</button>
<table>
    <caption>{caption}</caption>
    <thead>
        <tr>
            <th scope="col">Recipient IBAN</th>
            <th scope="col">Message to self</th>
            <th scope="col">Message to recipient</th>
            <th scope="col">Scheduled at Date & Time</th>
            <th scope="col">Amount</th>
        </tr>
    </thead>
    <tbody>
        {#each payments as payment}
            <tr>
                <td>{payment.recipientIban}</td>
                <td>{payment.messageToSelf}</td>
                <td>{payment.messageToRecipient}</td>
                <td>{payment.scheduledDateTime}</td>
                <td class="amount" class:positive={payment.paymentAmount.amount > 0}
                    >{payment.paymentAmount.amount} {payment.paymentAmount.currency}</td
                >
            </tr>
        {/each}
    </tbody>
    <tfoot>
        <tr>
            <td colspan="4">Total</td>
            <td>{payments.reduce((partialSum, p) => partialSum + p.paymentAmount.amount, 0)} {currency}</td>
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
