<template>
  <div class="dashboard">
    <div class="dashboard-grid">
      <div class="map-section">
        <MapView
          :locations="locations"
          :currentLocation="currentLocation"
          :predictedPath="predictedPath"
          :userId="userId"
          @location-update="handleLocationUpdate"
        />
      </div>

      <div class="panel prediction-panel">
        <h3>Prediction Engine</h3>
        <div v-if="prediction" class="prediction-content">
          <div class="current-zone">
            <span class="label">Current Zone:</span>
            <span class="value">{{ prediction.currentZone }}</span>
          </div>
          <div class="predicted-path">
            <span class="label">Predicted Path:</span>
            <div class="path-flow">
              <span v-for="(zone, idx) in prediction.predictedPath" :key="idx" class="zone-node">
                {{ zone }}
                <span v-if="idx < prediction.predictedPath.length - 1" class="arrow">→</span>
              </span>
            </div>
          </div>
          <div class="confidence">
            <span class="label">Confidence:</span>
            <span class="badge" :class="prediction.confidenceLevel.toLowerCase()">
              {{ prediction.confidenceLevel }}
            </span>
          </div>
          <div class="probability">
            <span class="label">Probability:</span>
            <span class="value">{{ (prediction.probability * 100).toFixed(1) }}%</span>
          </div>
        </div>
        <div v-else class="no-data">No prediction data available</div>
      </div>

      <div class="panel explanation-panel">
        <h3>AI Explanation</h3>
        <div v-if="explanation" class="explanation-content">
          <div class="prediction-text">{{ explanation.prediction }}</div>
          <div class="primary-reason">
            <span class="label">Primary Reason:</span>
            <p>{{ explanation.primaryReason }}</p>
          </div>
          <div class="secondary-factors">
            <span class="label">Secondary Factors:</span>
            <ul>
              <li v-for="(factor, idx) in explanation.secondaryFactors" :key="idx">
                {{ factor }}
              </li>
            </ul>
          </div>
        </div>
        <div v-else class="no-data">No explanation available</div>
      </div>

      <div class="panel alerts-panel">
        <h3>Alerts & Anomalies</h3>
        <div v-if="alerts.length > 0" class="alerts-list">
          <div v-for="(alert, idx) in alerts" :key="idx" class="alert-item" :class="alert.severity?.toLowerCase()">
            <span class="alert-type">{{ alert.type }}</span>
            <span class="alert-desc">{{ alert.description || alert.message }}</span>
            <span class="alert-time">{{ formatTime(alert.timestamp) }}</span>
          </div>
        </div>
        <div v-else class="no-data">No alerts</div>
      </div>

      <div class="panel chat-panel">
        <h3>Cognitive Query Engine</h3>
        <ChatInterface :userId="userId" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import MapView from '../components/MapView.vue'
import ChatInterface from '../components/ChatInterface.vue'
import { locationService, predictionService, alertService } from '../services/api'

const props = defineProps({
  userId: String
})

const locations = ref([])
const currentLocation = ref(null)
const prediction = ref(null)
const explanation = ref(null)
const alerts = ref([])
let watchId = null

const handleLocationUpdate = async (loc) => {
  currentLocation.value = loc
  locations.value.push(loc)

  try {
    await locationService.sendLocation({
      userId: props.userId,
      latitude: loc.lat,
      longitude: loc.lng,
      speed: loc.speed,
      accuracy: loc.accuracy
    })

    prediction.value = await predictionService.getPrediction(props.userId)
    explanation.value = await predictionService.getExplanation(props.userId)
  } catch (err) {
    console.error('Failed to sync location:', err)
  }
}

const loadAlerts = async () => {
  try {
    alerts.value = await alertService.getAlerts(props.userId)
  } catch (err) {
    console.error('Failed to load alerts:', err)
  }
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  return new Date(timestamp).toLocaleTimeString()
}

onMounted(() => {
  if ('geolocation' in navigator) {
    watchId = navigator.geolocation.watchPosition(
      (pos) => {
        handleLocationUpdate({
          lat: pos.coords.latitude,
          lng: pos.coords.longitude,
          speed: pos.coords.speed,
          accuracy: pos.coords.accuracy,
          timestamp: Date.now()
        })
      },
      (err) => console.error('Geolocation error:', err),
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 30000 }
    )
  }

  loadAlerts()
  setInterval(loadAlerts, 30000)
})

onUnmounted(() => {
  if (watchId !== null) {
    navigator.geolocation.clearWatch(watchId)
  }
})
</script>

<style scoped>
.dashboard {
  width: 100%;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 350px;
  grid-template-rows: auto auto auto;
  gap: 1.5rem;
}

.map-section {
  grid-row: span 3;
  min-height: 600px;
}

.panel {
  background: #1e293b;
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid #334155;
}

.panel h3 {
  font-size: 1rem;
  font-weight: 600;
  color: #38bdf8;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #334155;
}

.label {
  font-size: 0.75rem;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.value {
  color: #e2e8f0;
  font-weight: 500;
}

.prediction-content > div {
  margin-bottom: 1rem;
}

.path-flow {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.zone-node {
  background: #0f172a;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.875rem;
}

.arrow {
  color: #38bdf8;
}

.badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.badge.high { background: #166534; color: #86efac; }
.badge.medium { background: #854d0e; color: #fde047; }
.badge.low { background: #991b1b; color: #fca5a5; }

.primary-reason p {
  margin-top: 0.5rem;
  color: #e2e8f0;
  line-height: 1.5;
}

.secondary-factors ul {
  margin-top: 0.5rem;
  padding-left: 1.25rem;
}

.secondary-factors li {
  color: #94a3b8;
  margin-bottom: 0.25rem;
  font-size: 0.875rem;
}

.alerts-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  max-height: 300px;
  overflow-y: auto;
}

.alert-item {
  padding: 0.75rem 1rem;
  border-radius: 8px;
  border-left: 3px solid;
}

.alert-item.high { background: #1c1111; border-color: #ef4444; }
.alert-item.medium { background: #1c1611; border-color: #f59e0b; }
.alert-item.low { background: #111c1c; border-color: #06b6d4; }

.alert-type {
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.alert-desc {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.875rem;
}

.alert-time {
  font-size: 0.75rem;
  color: #64748b;
  margin-top: 0.25rem;
}

.no-data {
  color: #64748b;
  font-style: italic;
  text-align: center;
  padding: 2rem;
}
</style>
