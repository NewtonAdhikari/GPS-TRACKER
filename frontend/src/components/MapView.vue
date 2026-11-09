<template>
  <div class="map-container">
    <div ref="mapRef" class="map"></div>
    <div class="map-controls">
      <button @click="centerOnLocation" title="Center on my location">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10"/>
          <circle cx="12" cy="12" r="3"/>
          <line x1="12" y1="2" x2="12" y2="6"/>
          <line x1="12" y1="18" x2="12" y2="22"/>
          <line x1="2" y1="12" x2="6" y2="12"/>
          <line x1="18" y1="12" x2="22" y2="12"/>
        </svg>
      </button>
      <button @click="toggleHeatmap" :class="{ active: showHeatmap }" title="Toggle heatmap">
        Heatmap
      </button>
    </div>
    <div class="location-info">
      <div v-if="currentLocation">
        <span class="coord">{{ currentLocation.lat.toFixed(6) }}, {{ currentLocation.lng.toFixed(6) }}</span>
        <span v-if="currentLocation.speed" class="speed">{{ (currentLocation.speed * 3.6).toFixed(1) }} km/h</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, onUnmounted } from 'vue'
import L from 'leaflet'

const props = defineProps({
  locations: Array,
  currentLocation: Object,
  predictedPath: Array,
  userId: String
})

const emit = defineEmits(['location-update'])

const mapRef = ref(null)
const showHeatmap = ref(false)
let map = null
let locationMarker = null
let pathPolyline = null
let heatmapLayer = null

const initMap = () => {
  map = L.map(mapRef.value, {
    center: [20.0, 0.0],
    zoom: 2,
    zoomControl: true
  })

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
  }).addTo(map)
}

const updateLocationMarker = () => {
  if (!props.currentLocation || !map) return

  const latlng = [props.currentLocation.lat, props.currentLocation.lng]

  if (locationMarker) {
    locationMarker.setLatLng(latlng)
  } else {
    const icon = L.divIcon({
      className: 'current-location-marker',
      html: '<div class="pulse"></div><div class="dot"></div>',
      iconSize: [24, 24],
      iconAnchor: [12, 12]
    })
    locationMarker = L.marker(latlng, { icon }).addTo(map)
  }

  if (!showHeatmap.value) {
    map.panTo(latlng)
  }
}

const updatePath = () => {
  if (!props.locations || props.locations.length < 2 || !map) return

  if (pathPolyline) {
    map.removeLayer(pathPolyline)
  }

  const path = props.locations.map(l => [l.lat, l.lng])
  pathPolyline = L.polyline(path, {
    color: '#38bdf8',
    weight: 3,
    opacity: 0.7
  }).addTo(map)
}

const toggleHeatmap = () => {
  showHeatmap.value = !showHeatmap.value
  if (showHeatmap.value) {
    renderHeatmap()
  } else if (heatmapLayer) {
    map.removeLayer(heatmapLayer)
    heatmapLayer = null
  }
}

const renderHeatmap = () => {
  if (!props.locations || props.locations.length === 0 || !map) return

  if (heatmapLayer) {
    map.removeLayer(heatmapLayer)
  }

  const heatData = props.locations.map(l => [l.lat, l.lng, 1])

  heatmapLayer = L.layerGroup()
  heatData.forEach((point, i) => {
    const circle = L.circle(point, {
      radius: 100,
      fillColor: '#f97316',
      fillOpacity: 0.3,
      stroke: false
    })
    heatmapLayer.addLayer(circle)
  })

  heatmapLayer.addTo(map)
}

const centerOnLocation = () => {
  if (props.currentLocation && map) {
    map.setView([props.currentLocation.lat, props.currentLocation.lng], 15)
  }
}

watch(() => props.currentLocation, updateLocationMarker, { deep: true })
watch(() => props.locations, updatePath, { deep: true })

onMounted(() => {
  initMap()
})

onUnmounted(() => {
  if (map) {
    map.remove()
    map = null
  }
})
</script>

<style scoped>
.map-container {
  position: relative;
  height: 100%;
  min-height: 600px;
  border-radius: 12px;
  overflow: hidden;
}

.map {
  height: 100%;
  width: 100%;
}

.map-controls {
  position: absolute;
  top: 1rem;
  right: 1rem;
  z-index: 1000;
  display: flex;
  gap: 0.5rem;
}

.map-controls button {
  background: #1e293b;
  border: 1px solid #334155;
  color: #e2e8f0;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.2s;
}

.map-controls button:hover {
  background: #334155;
}

.map-controls button.active {
  background: #38bdf8;
  color: #0f172a;
}

.location-info {
  position: absolute;
  bottom: 1rem;
  left: 1rem;
  z-index: 1000;
  background: #1e293b;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  border: 1px solid #334155;
}

.coord {
  font-size: 0.875rem;
  font-family: monospace;
}

.speed {
  margin-left: 1rem;
  color: #38bdf8;
  font-size: 0.875rem;
}
</style>

<style>
.current-location-marker {
  background: none;
}

.current-location-marker .dot {
  width: 12px;
  height: 12px;
  background: #38bdf8;
  border-radius: 50%;
  border: 2px solid white;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.current-location-marker .pulse {
  width: 24px;
  height: 24px;
  background: rgba(56, 189, 248, 0.3);
  border-radius: 50%;
  position: absolute;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(0.5); opacity: 1; }
  100% { transform: scale(2); opacity: 0; }
}
</style>
