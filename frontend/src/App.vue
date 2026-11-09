<template>
  <div class="app">
    <header class="header">
      <h1>Cognitive GPS</h1>
      <span class="subtitle">Autonomous Predictive Intelligence System</span>
    </header>
    <main class="main-content">
      <Dashboard :userId="userId" />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Dashboard from './views/Dashboard.vue'

const userId = ref('demo-user-' + Math.random().toString(36).substr(2, 9))
</script>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #334155;
}

.header h1 {
  font-size: 1.75rem;
  font-weight: 700;
  background: linear-gradient(90deg, #38bdf8, #818cf8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 0.875rem;
  color: #64748b;
  margin-left: 1rem;
}

.main-content {
  flex: 1;
  padding: 1.5rem;
}
</style>
