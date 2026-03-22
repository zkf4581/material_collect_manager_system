<script setup lang="ts">
defineProps<{
  title: string
}>()
</script>

<template>
  <div class="mobile-shell">
    <header class="mobile-header">
      <h1>{{ title }}</h1>
    </header>
    <main class="mobile-content">
      <slot />
    </main>
  </div>
</template>

<style scoped>
.mobile-shell {
  min-height: 100dvh;
  background: var(--color-bg);
}

.mobile-header {
  position: sticky;
  top: 0;
  z-index: 10;
  padding: 16px;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
}

.mobile-header h1 {
  margin: 0;
  font-size: 18px;
  color: var(--color-text);
}

.mobile-content {
  padding: 16px;
}
</style>
