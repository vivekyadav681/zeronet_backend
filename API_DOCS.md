# ZeroNet — Backend API Requirements

> **Frontend Stack:** React + Vite + Tailwind CSS v4 + Zustand (state) + WebSocket (real-time)
> **This document defines every API the frontend needs to function.**

---

## Table of Contents
1. [Data Models](#data-models)
2. [REST APIs](#rest-apis)
   - [Authentication](#1-authentication)
   - [Organization Registration](#2-organization-registration)
   - [Dashboard Stats](#3-dashboard-stats)
   - [Incidents / SOS Alerts](#4-incidents--sos-alerts)
   - [Incident Details](#5-incident-details)
   - [Responders](#6-responders)
   - [Heatmap Data](#7-heatmap-data)
   - [Broadcast](#8-broadcast)
3. [WebSocket Events](#websocket-events)
4. [Integration Notes](#integration-notes)

---

## Data Models

### Organization
```json
{
  "id": "string (UUID)",
  "orgName": "string",
  "orgType": "Mall | Hotel | Hospital | Restaurant | Office | Stadium | Educational",
  "registrationId": "string (GSTIN / Official ID)",
  "email": "string",
  "phone": "string",
  "address": "string",
  "license": "string",
  "contactPerson": "string",
  "emergencyContact": "string",
  "website": "string",
  "startTime": "string (HH:mm)",
  "endTime": "string (HH:mm)",
  "is24Hours": "boolean",
  "totalFloors": "number",
  "securityStaffCount": "number",
  "city": "string",
  "state": "string",
  "coords": { "lat": "number", "lng": "number" },
  "radius": "number (meters, geofence range)"
}
```

### Incident (SOS Alert)
```json
{
  "id": "string (UUID)",
  "sosId": "string (e.g. SOS-8821)",
  "caseName": "string (title)",
  "description": "string",
  "location": "string (readable address)",
  "coords": { "lat": "number", "lng": "number" },
  "type": "string (e.g. Critical Medical Response, Fire, Accident, etc.)",
  "severity": "critical | high | moderate | low",
  "status": "reported | accepted | in_progress | resolved",
  "elapsedTime": "string (human readable) OR createdAt timestamp",
  "createdAt": "ISO 8601 timestamp",
  "organizationId": "string (FK)",
  "responders": [
    { "name": "string", "status": "string" }
  ],
  "timeline": [
    { "event": "string", "timestamp": "ISO 8601" }
  ]
}
```

### Responder
```json
{
  "id": "string (UUID)",
  "name": "string",
  "role": "string (Engine, Ambulance, Patrol, etc.)",
  "status": "available | dispatched | on_scene | off_duty",
  "coords": { "lat": "number", "lng": "number" },
  "assignedIncidentId": "string | null"
}
```

### Broadcast
```json
{
  "id": "string (UUID)",
  "message": "string (text content)",
  "audioUrl": "string | null (URL to audio file)",
  "type": "text | voice | emergency",
  "organizationId": "string (FK)",
  "createdAt": "ISO 8601",
  "status": "active | stopped"
}
```

---

## REST APIs

> **Base URL suggestion:** `https://api.zeronet.in/v1`
> **Auth:** Bearer token (JWT) in `Authorization` header for all protected routes.

---

### 1. Authentication

#### `POST /auth/login`
**Used on:** Login page

| Field | Type | Required |
|-------|------|----------|
| `email` OR `phone` | string | ✅ |
| `password` | string | ✅ (for password mode) |
| `rememberDevice` | boolean | ❌ |

**Response:**
```json
{
  "token": "JWT string",
  "user": {
    "id": "string",
    "name": "string",
    "email": "string",
    "organizationId": "string",
    "role": "admin | responder | viewer"
  }
}
```

---

#### `POST /auth/login/otp/request`
**Used on:** Login page ("Switch to OTP" button)

| Field | Type | Required |
|-------|------|----------|
| `phone` OR `email` | string | ✅ |

**Response:**
```json
{ "message": "OTP sent", "expiresIn": 30 }
```

---

#### `POST /auth/login/otp/verify`
**Used on:** Login page (OTP verification)

| Field | Type | Required |
|-------|------|----------|
| `phone` OR `email` | string | ✅ |
| `otp` | string (6 digits) | ✅ |

**Response:** Same as `/auth/login` response (returns JWT + user).

---

### 2. Organization Registration

#### `POST /auth/register/send-otp`
**Used on:** Register page ("Verify & Continue" button)

| Field | Type | Required |
|-------|------|----------|
| `email` | string | ✅ |
| `phone` | string | ✅ |
| `registrationId` | string | ✅ |

**Response:**
```json
{ "message": "OTP sent to email/phone", "expiresIn": 30 }
```

---

#### `POST /auth/register/verify-otp`
**Used on:** Register page (OTP input)

| Field | Type | Required |
|-------|------|----------|
| `email` | string | ✅ |
| `otp` | string (6 digits) | ✅ |

**Response:**
```json
{ "verified": true }
```

---

#### `POST /v1/auth/register/organization`
**Used on:** Register page for Organizations ("Finalize Registration" button)

> [!IMPORTANT]
> This is the main registration payload. All fields from the Organization model are sent here. Assigns `admin` role.

**Request Body:**
```json
{
  "orgName": "Grand Plaza Mall",
  "orgType": "Mall",
  "registrationId": "GSTIN123456",
  "email": "admin@grandplaza.com",
  "phone": "+91-9876543210",
  "address": "Plot 42, Sector 18, Noida",
  "license": "BL-2024-001",
  "contactPerson": "Rahul Sharma",
  "emergencyContact": "+91-1122334455",
  "website": "https://grandplaza.com",
  "startTime": "09:00",
  "endTime": "21:00",
  "is24Hours": false,
  "totalFloors": 4,
  "securityStaffCount": 25,
  "city": "Noida",
  "state": "Uttar Pradesh",
  "coords": { "lat": 28.5706, "lng": 77.3218 },
  "radius": 50
}
```

**Response:**
```json
{
  "token": "JWT string",
  "organization": { "id": "...", "orgName": "..." },
  "user": { "id": "...", "role": "admin" }
}
```

---

#### `POST /v1/auth/register/user`
**Used on:** Register page for General Users

**Request Body:**
```json
{
  "email": "user@example.com",
  "name": "Jane Doe",
  "password": "SecurePassword123"
}
```

**Response:**
```json
{
  "message": "User registered successfully. Please verify your email/phone."
}
```
> Assigns `viewer` role.

---

### 3. Dashboard Stats

#### `GET /dashboard/stats`
**Used on:** Dashboard page (4 stat cards at the top)

**Query Params:** `?organizationId=xxx`

**Response:**
```json
{
  "activeSosAlerts": { "count": 4, "changePercent": 24, "changeDirection": "up" },
  "highPriorityIncidents": { "count": 12, "description": "Requiring immediate action" },
  "respondersInField": { "count": 87, "connectionRate": 92 },
  "todayResolved": { "count": 142, "chartData": [28, 34, 56, 28, 25, 78, 12] }
}
```

> [!NOTE]
> `chartData` is an array of 7 values representing resolution counts over the past 7 time periods (hours/days as decided). This feeds a bar chart on the frontend.

---

### 4. Incidents / SOS Alerts

#### `GET /incidents`
**Used on:** Dashboard (case cards), Organization (venue alerts), LiveMap (stream panel), Responder (incident list)

**Query Params:**

| Param | Type | Description |
|-------|------|------------|
| `organizationId` | string | Filter by organization |
| `status` | string | `reported`, `accepted`, `in_progress`, `resolved`, or `all` |
| `type` | string | `Emergency`, `Fire`, `Accident`, `Medical`, `Flood`, `Stampede` |
| `timeRange` | string | `1h`, `4h`, `8h`, `12h`, `24h`, `3d`, `1w`, `1m`, `3m`, `6m`, `12m` |
| `page` | number | Pagination |
| `limit` | number | Items per page |

**Response:**
```json
{
  "incidents": [
    {
      "id": "uuid",
      "sosId": "SOS-8821",
      "caseName": "Multiple Vehicle Collision",
      "description": "Patient found unconscious on the road.",
      "location": "Intersection of 5th & Broadway",
      "coords": { "lat": 28.620, "lng": 77.210 },
      "type": "Critical Medical Response",
      "severity": "critical",
      "status": "reported",
      "createdAt": "2026-04-23T10:00:00Z",
      "elapsedTime": "03:45 mins"
    }
  ],
  "total": 45,
  "page": 1
}
```

---

#### `PATCH /incidents/:id/status`
**Used on:** IncidentDetails page ("Update Status"), Responder page ("Accept", "Resolved")

**Request Body:**
```json
{
  "status": "accepted | in_progress | resolved",
  "responderId": "string (optional — who's updating)"
}
```

**Response:**
```json
{ "id": "...", "status": "accepted", "updatedAt": "..." }
```

---

#### `PATCH /incidents/:id/escalate`
**Used on:** IncidentDetails page ("Escalate Severity" button)

**Request Body:**
```json
{
  "severity": "critical",
  "reason": "string (optional)"
}
```

---

#### `POST /incidents/:id/assign`
**Used on:** Dashboard CaseAlertCard ("Assign" button), Organization venue alerts ("Assign Staff" button)

**Request Body:**
```json
{
  "responderId": "string"
}
```

---

#### `POST /incidents/:id/resolve`
**Used on:** Dashboard CaseAlertCard ("Resolve" button)

**Request Body:**
```json
{
  "resolvedBy": "string (userId)",
  "notes": "string (optional)"
}
```

---

### 5. Incident Details

#### `GET /incidents/:id`
**Used on:** IncidentDetails page (full detail view)

> [!NOTE]
> Currently the frontend navigates with route state from the card click. But this API is needed for deep-linking (sharing incident URLs) and for loading full details like responders list and timeline.

**Response:**
```json
{
  "id": "uuid",
  "sosId": "SOS-8821",
  "caseName": "Multiple Vehicle Collision",
  "description": "Patient found unconscious...",
  "location": "Intersection of 5th & Broadway",
  "coords": { "lat": 28.620, "lng": 77.210 },
  "type": "Critical Medical Response",
  "severity": "critical",
  "status": "in_progress",
  "createdAt": "2026-04-23T10:00:00Z",
  "responders": [
    { "id": "r1", "name": "Engine 42", "status": "Active Suppression" },
    { "id": "r2", "name": "Ambulance R7", "status": "Medical Standby" },
    { "id": "r3", "name": "Patrol 201", "status": "Traffic Control" }
  ],
  "timeline": [
    { "event": "Initial Alert Broadcasted", "timestamp": "2026-04-23T10:00:00Z" },
    { "event": "Fire Engine Arrived", "timestamp": "2026-04-23T10:05:30Z" },
    { "event": "Perimeter Established", "timestamp": "2026-04-23T10:08:00Z" }
  ]
}
```

---

### 6. Responders

#### `GET /responders`
**Used on:** Responder page (incident list with coords for map markers)

**Query Params:** `?organizationId=xxx&status=available`

**Response:**
```json
{
  "responders": [
    {
      "id": "r1",
      "name": "Engine 42",
      "role": "Fire Engine",
      "status": "dispatched",
      "coords": { "lat": 28.6139, "lng": 77.3910 },
      "assignedIncidentId": "inc-001"
    }
  ]
}
```

---

### 7. Heatmap Data

#### `GET /heatmap`
**Used on:** Heatmap page (Google Maps HeatmapLayer)

**Query Params:**

| Param | Type | Description |
|-------|------|------------|
| `timeRange` | string | `today`, `week`, `month` |
| `type` | string | `all`, `critical`, `moderate`, `low` |
| `region` | string | `metropolitan`, `district` (optional) |
| `organizationId` | string | Optional filter |

**Response:**
```json
{
  "points": [
    { "lat": 19.0760, "lng": 72.8777, "type": "critical", "weight": 1.0 },
    { "lat": 13.0827, "lng": 80.2707, "type": "moderate", "weight": 0.6 }
  ],
  "stats": {
    "totalHotspots": 30,
    "avgResponseTime": "4.2m"
  }
}
```

> [!TIP]
> The `weight` field is optional but useful for Google Maps HeatmapLayer to show intensity differences. Heavier weight = more intense color.

---

### 8. Broadcast

#### `POST /broadcast/text`
**Used on:** Organization page → Broadcast System ("SEND TEXT" button)

**Request Body:**
```json
{
  "organizationId": "string",
  "message": "string"
}
```

---

#### `POST /broadcast/voice`
**Used on:** Organization page → Broadcast System ("LIVE VOICE" button after recording)

**Request:** `multipart/form-data`

| Field | Type | Description |
|-------|------|------------|
| `organizationId` | string | Organization identifier |
| `audio` | File (audio/webm) | Recorded audio blob |

**Response:**
```json
{ "id": "broadcast-uuid", "audioUrl": "https://...", "status": "active" }
```

---

#### `POST /broadcast/emergency`
**Used on:** Organization page → "TRIGGER EMERGENCY" button

**Request Body:**
```json
{
  "organizationId": "string"
}
```

> [!CAUTION]
> This is the highest-priority action in the system. It should instantly notify ALL users and responders within the organization's geofenced zone. Consider rate-limiting and requiring confirmation.

**Response:**
```json
{ "id": "emergency-uuid", "status": "triggered", "notifiedCount": 142 }
```

---

#### `POST /broadcast/stop`
**Used on:** LiveMap broadcast bar ("STOP" button)

**Request Body:**
```json
{ "broadcastId": "string" }
```

---

## WebSocket Events

> **Connection URL suggestion:** `wss://api.zeronet.in/ws?token=JWT&orgId=xxx`
> **Library on frontend:** `react-use-websocket` (already installed)

### Connection Flow
```
1. Frontend connects with JWT token + organizationId
2. Server authenticates and subscribes client to org channel
3. Server pushes events in real-time
4. Frontend updates Zustand store on each event
```

---

### Events: Server → Client (RECEIVE)

| Event Name | When Fired | Payload |
|------------|-----------|---------|
| `NEW_SOS` | New SOS alert reported by a user | Full `Incident` object |
| `INCIDENT_STATUS_UPDATE` | Incident status changes (accepted, in_progress, resolved) | `{ incidentId, status, updatedBy, timestamp }` |
| `INCIDENT_ESCALATED` | Severity escalated | `{ incidentId, newSeverity, reason, timestamp }` |
| `RESPONDER_ASSIGNED` | Responder assigned to an incident | `{ incidentId, responderId, responderName, timestamp }` |
| `RESPONDER_LOCATION_UPDATE` | Responder GPS position updated | `{ responderId, coords: { lat, lng }, timestamp }` |
| `BROADCAST_RECEIVED` | Text/voice broadcast sent | `{ broadcastId, message, audioUrl, type, timestamp }` |
| `EMERGENCY_TRIGGERED` | Venue-wide emergency activated | `{ organizationId, triggeredBy, timestamp }` |
| `BROADCAST_STOPPED` | Active broadcast stopped | `{ broadcastId, stoppedBy, timestamp }` |
| `STATS_UPDATE` | Dashboard counters changed | Same shape as `GET /dashboard/stats` response |

### Events: Client → Server (SEND)

| Event Name | When Sent | Payload |
|------------|----------|---------|
| `SUBSCRIBE_ORG` | On connect, to join org channel | `{ organizationId }` |
| `ACCEPT_INCIDENT` | Responder accepts an incident | `{ incidentId, responderId }` |
| `UPDATE_LOCATION` | Responder shares GPS (periodic) | `{ responderId, coords: { lat, lng } }` |

> [!IMPORTANT]
> The `NEW_SOS` event is the most critical real-time event. When this fires, the frontend immediately:
> 1. Adds the incident to the Dashboard case list
> 2. Adds a marker on LiveMap
> 3. Shows a stream entry in the LiveMap/Responder side panels
> 4. Increments the "Active SOS Alerts" stat counter
> 5. Optionally plays an audio notification

---

## Pages → API Mapping

| Page | REST APIs Used | WebSocket Events Listened |
|------|---------------|--------------------------|
| **Login** | `POST /auth/login`, `POST /auth/login/otp/*` | — |
| **Register** | `POST /auth/register/send-otp`, `POST /auth/register/verify-otp`, `POST /auth/register` | — |
| **Dashboard** | `GET /dashboard/stats`, `GET /incidents` | `NEW_SOS`, `INCIDENT_STATUS_UPDATE`, `STATS_UPDATE` |
| **LiveMap** | `GET /incidents` (for markers + stream) | `NEW_SOS`, `INCIDENT_STATUS_UPDATE`, `BROADCAST_RECEIVED`, `BROADCAST_STOPPED` |
| **Heatmap** | `GET /heatmap` | — |
| **IncidentDetails** | `GET /incidents/:id`, `PATCH /incidents/:id/status`, `PATCH /incidents/:id/escalate` | `INCIDENT_STATUS_UPDATE`, `RESPONDER_ASSIGNED` |
| **Responder** | `GET /incidents`, `GET /responders` | `NEW_SOS`, `RESPONDER_LOCATION_UPDATE`, `INCIDENT_STATUS_UPDATE` |
| **Organization** | `GET /incidents`, `POST /broadcast/*`, `POST /broadcast/emergency` | `NEW_SOS`, `BROADCAST_RECEIVED`, `EMERGENCY_TRIGGERED` |

---

## Integration Notes

### State Management (Zustand)
The frontend will use **Zustand** stores for:
- `useAuthStore` — JWT token, user info, login/logout
- `useIncidentStore` — All incidents, filtered lists, selected incident  
- `useStatsStore` — Dashboard stat counters (updated via WS)
- `useBroadcastStore` — Active broadcast state
- `useResponderStore` — Responder list and positions

### Error Response Format
Please return errors consistently:
```json
{
  "error": true,
  "code": "INVALID_OTP",
  "message": "The OTP entered is incorrect or expired."
}
```

### HTTP Status Codes Expected
| Code | Meaning |
|------|---------|
| `200` | Success |
| `201` | Created (registration, new broadcast) |
| `400` | Validation error |
| `401` | Unauthorized (invalid/expired JWT) |
| `403` | Forbidden (wrong role) |
| `404` | Resource not found |
| `429` | Rate limited (especially for emergency trigger) |
| `500` | Server error |

### CORS
Frontend runs on `http://localhost:5173` (dev) and will be deployed to a custom domain. Please allow CORS from both.

### File Uploads
Voice broadcasts send audio as `multipart/form-data` with MIME type `audio/webm`. Backend should store and return a CDN/public URL.
