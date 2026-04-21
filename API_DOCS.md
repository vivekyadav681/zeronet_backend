# Zeronet API Documentation

This document outlines all the available API endpoints in the Zeronet application, along with expected request formats and response examples.

---

## 1. Authentication Endpoints (`/api/auth`)

### 1.1 Register User
**Endpoint:** `POST /api/auth/register`
**Description:** Registers a new user and user profile.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123",
  "name": "John Doe"
}
```

**Response (200 OK):**
```json
{
  "token": null,
  "message": "User registered successfully. Please verify OTP sent to email."
}
```

### 1.2 Verify OTP
**Endpoint:** `POST /api/auth/verify-otp`
**Description:** Verifies the OTP sent to the user's email during registration.

**Request Body:**
```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "OTP verified successfully"
}
```

### 1.3 Login
**Endpoint:** `POST /api/auth/login`
**Description:** Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful"
}
```

### 1.4 Refresh Token
**Endpoint:** `POST /api/auth/refresh-token`
**Description:** Generates a new JWT token using a valid existing token.

**Request Body:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Token refreshed"
}
```

---

## 2. Incident Endpoints (`/api/incidents`)

### 2.1 Create Incident
**Endpoint:** `POST /api/incidents`
**Description:** Creates a new incident report.

**Request Body:**
```json
{
  "senderId": 1,
  "victimId": 1,
  "incidentDateTime": "2026-04-21T10:30:00",
  "latitude": 40.712776,
  "longitude": -74.005974,
  "description": "Medical emergency on 5th Avenue",
  "severity": "HIGH"
}
```

**Response (200 OK):**
```json
{
  "id": 101,
  "senderId": 1,
  "victimId": 1,
  "incidentDateTime": "2026-04-21T10:30:00",
  "latitude": 40.712776,
  "longitude": -74.005974,
  "description": "Medical emergency on 5th Avenue",
  "severity": "HIGH",
  "status": "OPEN",
  "createdAt": "2026-04-21T10:31:00"
}
```

### 2.2 Update Incident Status
**Endpoint:** `PUT /api/incidents/{id}/status`
**Description:** Updates the status of an existing incident.

**Request Body:**
```json
{
  "status": "RESOLVED"
}
```

**Response (200 OK):**
```json
{
  "id": 101,
  "senderId": 1,
  "victimId": 1,
  "incidentDateTime": "2026-04-21T10:30:00",
  "latitude": 40.712776,
  "longitude": -74.005974,
  "description": "Medical emergency on 5th Avenue",
  "severity": "HIGH",
  "status": "RESOLVED",
  "createdAt": "2026-04-21T10:31:00"
}
```

### 2.3 Get User Incidents
**Endpoint:** `GET /api/incidents/user/{userId}`
**Description:** Fetches all incidents where the user is either the sender or the victim.

**Response (200 OK):**
```json
[
  {
    "id": 101,
    "senderId": 1,
    "victimId": 1,
    "incidentDateTime": "2026-04-21T10:30:00",
    "latitude": 40.712776,
    "longitude": -74.005974,
    "description": "Medical emergency on 5th Avenue",
    "severity": "HIGH",
    "status": "RESOLVED",
    "createdAt": "2026-04-21T10:31:00"
  }
]
```

### 2.4 Delete Incident
**Endpoint:** `DELETE /api/incidents/{id}`
**Description:** Deletes an incident by its ID.

**Response (204 No Content):**
*(No body returned)*

---

## 3. Help Endpoints (`/api/help`)

### 3.1 Accept Help
**Endpoint:** `POST /api/help/accept`
**Description:** Accepts a help request for an incident. Updates the incident status to `IN_PROGRESS` and returns victim details.

**Request Body:**
```json
{
  "incidentId": 101
}
```

**Response (200 OK):**
```json
{
  "latitude": 40.712776,
  "longitude": -74.005974,
  "victimName": "John Doe",
  "victimPhone": "+1234567890",
  "emergencyContact1Name": "Jane Doe",
  "emergencyContact1Phone": "+0987654321",
  "incidentDescription": "Medical emergency on 5th Avenue"
}
```

### 3.2 Send Help Email Notification
**Endpoint:** `POST /api/help/send`
**Description:** Dispatches an email notification containing victim location and details to a specified recipient.

**Request Body:**
```json
{
  "incidentId": 101,
  "recipientEmail": "responder@example.com"
}
```

**Response (200 OK):**
*(No body returned, status 200 means email sent successfully)*

---

## 4. User Profile Endpoints (`/api/profiles`)

### 4.1 Get User Profile
**Endpoint:** `GET /api/profiles/{userId}`
**Description:** Retrieves a user's profile details.

**Response (200 OK):**
```json
{
  "name": "John Doe",
  "phoneNumber": "+1234567890",
  "hometown": "New York",
  "dateOfBirth": "1990-01-01",
  "emergencyContact1Name": "Jane Doe",
  "emergencyContact1Phone": "+0987654321",
  "emergencyContact2Name": null,
  "emergencyContact2Phone": null,
  "emergencyContact3Name": null,
  "emergencyContact3Phone": null
}
```

### 4.2 Update User Profile
**Endpoint:** `PUT /api/profiles/{userId}`
**Description:** Updates a user's profile details.

**Request Body:**
```json
{
  "name": "John Doe",
  "phoneNumber": "+1999999999",
  "hometown": "Los Angeles",
  "dateOfBirth": "1990-01-01",
  "emergencyContact1Name": "Jane Doe",
  "emergencyContact1Phone": "+0987654321"
}
```

**Response (200 OK):**
```json
{
  "name": "John Doe",
  "phoneNumber": "+1999999999",
  "hometown": "Los Angeles",
  "dateOfBirth": "1990-01-01",
  "emergencyContact1Name": "Jane Doe",
  "emergencyContact1Phone": "+0987654321",
  "emergencyContact2Name": null,
  "emergencyContact2Phone": null,
  "emergencyContact3Name": null,
  "emergencyContact3Phone": null
}
```
