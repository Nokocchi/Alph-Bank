<script lang="ts">
    import type { PageData } from "./$types";

    export let data: PageData;
</script>

Your loan application has been received. This does not mean it has been approved. We will first need to perform a credit check, after which
your application will be either approved or denied.

<a href="/customer/{data.customerId}/loan">Check your ongoing loan applications here</a>
