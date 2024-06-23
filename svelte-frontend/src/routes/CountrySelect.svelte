<script lang="ts">
    import type { CountryCodeAndCountry } from "../types";

  let countries: CountryCodeAndCountry[] = [
    {countryCode: "DK", asText: "Danmark"},
    {countryCode: "NO", asText: "Norge"},
    {countryCode: "SE", asText: "Sverige"},
    {countryCode: "FI", asText: "Suomi"},
  ];
  let countryCodeIndex: number = 0;

  export let country = countries[countryCodeIndex];
  
</script>

<select bind:value={countryCodeIndex}>
  {#each countries as country, i}
    <option value={i}>{country.asText}</option>
  {/each}
</select>
