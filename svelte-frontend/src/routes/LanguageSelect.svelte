<script lang="ts">
  import { languageStore } from "./stores";
  let languages: string[] = ["da", "nb", "sv", "fi"];
  let languageIndex: number = 0;

  $: {
    $languageStore = languages[languageIndex];
  }
</script>

<select bind:value={languageIndex}>
  {#each languages as language, i}
    <option value={i}>{language}</option>
  {/each}
</select>
