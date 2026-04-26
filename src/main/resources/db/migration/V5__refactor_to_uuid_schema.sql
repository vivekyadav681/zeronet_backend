-- V5__refactor_to_uuid_schema.sql
-- WARNING: This migration drops existing tables to migrate to UUID-based schema.

DROP TABLE IF EXISTS incident_responders;
DROP TABLE IF EXISTS incidents;
DROP TABLE IF EXISTS user_profiles;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS responders CASCADE;
DROP TABLE IF EXISTS broadcasts CASCADE;
-- organizations is already UUID, but let's make sure it links.

CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    name VARCHAR(255),
    phone VARCHAR(255),
    organization_id UUID,
    role VARCHAR(50),
    otp VARCHAR(32),
    otp_expiry TIMESTAMP,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE responders (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    status VARCHAR(50),
    lat DOUBLE PRECISION,
    lng DOUBLE PRECISION,
    assigned_incident_id UUID,
    organization_id UUID
);

CREATE TABLE incidents (
    id UUID PRIMARY KEY,
    sos_id VARCHAR(255) UNIQUE,
    case_name VARCHAR(255),
    description TEXT,
    location VARCHAR(255),
    lat DOUBLE PRECISION,
    lng DOUBLE PRECISION,
    type VARCHAR(255),
    severity VARCHAR(50),
    status VARCHAR(50),
    elapsed_time VARCHAR(255),
    organization_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE incident_timeline (
    id UUID PRIMARY KEY,
    incident_id UUID,
    event VARCHAR(255),
    event_timestamp TIMESTAMP
);

CREATE TABLE broadcasts (
    id UUID PRIMARY KEY,
    message TEXT,
    audio_url VARCHAR(255),
    type VARCHAR(50),
    organization_id UUID,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE incident_responders (
    incident_id UUID,
    responder_id UUID,
    PRIMARY KEY (incident_id, responder_id)
);
