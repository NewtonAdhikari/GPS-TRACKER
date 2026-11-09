<<<<<<< HEAD
# Autonomous Cognitive GPS System

**An intelligent GPS-based system with predictive intelligence, explainable decision engine, and adaptive behavior modeling.**

---

## Overview

This system tracks real-time user location, learns movement patterns, predicts future locations, detects anomalies, and explains its reasoning transparently.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 21, Spring Boot 3.4, Spring Data MongoDB |
| Database | MongoDB 8.0 with GeoJSON & Geospatial Indexing |
| Frontend | Vue.js 3 (Composition API), Leaflet.js, Axios |
| Real-time | WebSocket (STOMP protocol) |
| NLP | Rule-based chatbot |

---

## Core Modules

### 1. Location Ingestion Module
- REST API: `POST /api/location`
- Stores GPS data in MongoDB (GeoJSON format)

### 2. Dynamic Zone Clustering Engine
- Automatically groups coordinates into zones
- Distance-based clustering algorithm

### 3. Predictive Intelligence Engine
- Markov Chain model for multi-step prediction
- Outputs predicted path (not just single location)

### 4. Intent Detection Engine
- Infers intent using time of day, zone type, and history
- Example: "Intent: Going to Work"

### 5. Explainable Decision Engine (XAI)
- **Primary reason** (pattern probability)
- **Secondary factors** (time, frequency)
- **Confidence level** (HIGH/MEDIUM/LOW)

### 6. Simulation Engine
- Generates future movement scenarios
- API: `GET /api/simulate`

### 7. Anomaly Detection Engine
- GPS spoofing detection (impossible speed)
- Route deviation detection
- Sudden stop detection

### 8. Emergency Detection System
- No movement alerts
- Unusual pattern alerts

### 9. Trust Score Engine
- Assigns confidence score to predictions
- Based on pattern strength, data consistency, recency

### 10. Cognitive Query Engine
- Rule-based NLP chatbot
- Example queries: "Where am I going?", "Why this prediction?"

---

## API Endpoints

```
POST   /api/location    - Store GPS data
GET    /api/predict     - Get movement prediction
GET    /api/explain     - Get AI explanation
GET    /api/history     - Get location history
GET    /api/simulate    - Simulate future path
GET    /api/anomalies   - Get detected anomalies
GET    /api/alerts      - Get emergency alerts
POST   /api/chat        - Chat with the system
```

---

## MongoDB Schema

### user_locations
```json
{
  "userId": "string",
  "location": { "type": "Point", "coordinates": [lng, lat] },
  "timestamp": "ISODate",
  "speed": "number"
}
```

### dynamic_zones
```json
{
  "userId": "string",
  "zoneId": "string",
  "centroid": [lng, lat],
  "visitCount": "number"
}
```

### transition_matrix
```json
{
  "userId": "string",
  "transitions": { "zoneA": { "zoneB": count } }
}
```

### predictions
```json
{
  "userId": "string",
  "currentZone": "string",
  "predictedPath": ["zoneA", "zoneB"],
  "probability": 0.75,
  "confidenceLevel": "HIGH",
  "reasons": ["Primary reason", "..."]
}
```

---

## Quick Start

See [SETUP.md](./SETUP.md) for detailed instructions.

```bash
# 1. Start MongoDB
mongod --dbpath "C:\data\db"

# 2. Build & Run Backend
cd backend
mvn spring-boot:run

# 3. Build & Run Frontend
cd frontend
npm install
npm run dev
```

---

## Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Browser   │────▶│  Spring     │────▶│   MongoDB   │
│  Geolocation│◀────│  Boot API   │◀────│  (GeoJSON)  │
└─────────────┘     └──────┬──────┘     └─────────────┘
                           │
                    ┌──────▼──────┐
                    │  WebSocket  │
                    │  (STOMP)    │
                    └─────────────┘
```

---

## Key Features

- **Real-time location tracking** via browser Geolocation API
- **Live map** with Leaflet.js and OpenStreetMap
- **Movement path visualization** (polyline)
- **Multi-step future path prediction**
- **AI explanation panel** showing reasoning
- **Heatmap** of visited locations
- **Alerts panel** for anomalies/emergencies
- **Chat interface** for cognitive queries

---

## License

MIT License - Academic/Free Use
=======
# GPS-TRACKER
>>>>>>> b39fa1a325a5b6e78d8ab43180337e57367f2381
