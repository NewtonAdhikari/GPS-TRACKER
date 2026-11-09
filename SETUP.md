# Autonomous Cognitive GPS System

## Project Structure

```
GPS Tracker/
├── backend/                    # Spring Boot Backend
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/cognitivegps/
│       │   ├── CognitiveGpsApplication.java
│       │   ├── config/
│       │   │   ├── WebConfig.java
│       │   │   ├── WebSocketConfig.java
│       │   │   └── GlobalExceptionHandler.java
│       │   ├── controller/
│       │   │   ├── LocationController.java
│       │   │   ├── PredictionController.java
│       │   │   ├── SimulationController.java
│       │   │   ├── AlertController.java
│       │   │   ├── ChatController.java
│       │   │   └── WebSocketController.java
│       │   ├── dto/
│       │   ├── model/
│       │   ├── repository/
│       │   └── service/
│       └── resources/
│           └── application.properties
│
├── frontend/                   # Vue.js Frontend
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── main.js
│       ├── App.vue
│       ├── components/
│       │   ├── MapView.vue
│       │   └── ChatInterface.vue
│       ├── views/
│       │   └── Dashboard.vue
│       ├── services/
│       │   └── api.js
│       └── router/
│
├── SETUP.md
└── README.md
```

---

## Prerequisites

- **Java 21** (or JDK 21)
- **Maven 3.9+**
- **MongoDB 6.0+** (running on localhost:27017)
- **Node.js 18+** (for frontend)

---

## Step 1: Start MongoDB

```bash
# If MongoDB is installed locally
mongod --dbpath "C:\data\db" --port 27017

# Or use MongoDB Compass/Atlas connection string in application.properties
```

---

## Step 2: Build & Run Backend

```bash
# Navigate to backend directory
cd backend

# Set JAVA_HOME (Windows)
export JAVA_HOME="/c/Program Files/Java/jdk-21.0.10"
export PATH="$JAVA_HOME/bin:$PATH"

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend starts on **http://localhost:8080**

---

## Step 3: Build & Run Frontend

```bash
# Open a new terminal
cd frontend

# Install dependencies
npm install

# Run development server
npm run dev
```

The frontend starts on **http://localhost:5173**

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/location` | Store GPS location |
| GET | `/api/predict` | Get movement prediction |
| GET | `/api/explain` | Get AI explanation |
| GET | `/api/history` | Get location history |
| GET | `/api/simulate` | Simulate future path |
| GET | `/api/anomalies` | Get detected anomalies |
| GET | `/api/alerts` | Get emergency alerts |
| POST | `/api/chat` | Chat with the system |

---

## MongoDB Collections

- `user_locations` - GPS coordinates with GeoJSON
- `dynamic_zones` - Clustered movement zones
- `transition_matrix` - Markov chain transition data
- `behavior_profiles` - User behavior patterns
- `predictions` - Movement predictions
- `anomalies` - Detected anomalies (SPOOF/DEVIATION/STOP)
- `simulations` - Simulated paths
- `alerts` - Emergency alerts

---

## Features

1. **Real-time Location Tracking** - Browser Geolocation API integration
2. **Dynamic Zone Clustering** - Automatic zone creation based on movement
3. **Markov Chain Prediction** - Multi-step path prediction
4. **Intent Detection** - Time and context-based intent inference
5. **Explainable AI** - Layered reasoning for predictions
6. **Anomaly Detection** - GPS spoofing and route deviation detection
7. **Emergency Detection** - Long-duration stop alerts
8. **Simulation Engine** - What-if future scenarios
9. **Chat Interface** - Rule-based NLP query engine
10. **WebSocket Support** - Real-time updates

---

## Sample Data (for testing)

Insert sample location data into MongoDB:

```javascript
db.user_locations.insertMany([
  {
    userId: "demo-user",
    location: { type: "Point", coordinates: [-73.9857, 40.7484] },
    timestamp: new Date(),
    speed: 45.2
  }
])
```

---

## Development Notes

- The system auto-learns from location data
- More data = better predictions
- Trust score increases with consistent usage
- WebSocket endpoint: `/ws` with STOMP protocol
