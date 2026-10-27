CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(100),
    locale     VARCHAR(10) NOT NULL DEFAULT 'pt-BR',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE vehicles (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    brand      VARCHAR(100) NOT NULL,
    model      VARCHAR(100) NOT NULL,
    year       SMALLINT,
    plate      VARCHAR(20),
    fuel_type  VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_vehicles_user_id ON vehicles(user_id);

CREATE TABLE fuel_fillups (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id      UUID NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    date            DATE NOT NULL,
    odometer_km     NUMERIC(10,2) NOT NULL,
    total_liters    NUMERIC(10,3) NOT NULL,
    price_per_liter NUMERIC(10,4) NOT NULL,
    total_cost      NUMERIC(10,2) GENERATED ALWAYS AS (total_liters * price_per_liter) STORED,
    fuel_type       VARCHAR(20) NOT NULL,
    full_tank       BOOLEAN NOT NULL DEFAULT TRUE,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fuel_fillups_vehicle_id ON fuel_fillups(vehicle_id);
CREATE INDEX idx_fuel_fillups_date ON fuel_fillups(date);

CREATE TABLE maintenance_schedules (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id      UUID NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    type            VARCHAR(100) NOT NULL,
    description     TEXT,
    next_km         NUMERIC(10,2),
    next_date       DATE,
    interval_km     NUMERIC(10,2),
    interval_months SMALLINT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_maint_schedules_vehicle_id ON maintenance_schedules(vehicle_id);

CREATE TABLE maintenance_expenses (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id  UUID NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    date        DATE NOT NULL,
    category    VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    cost        NUMERIC(10,2) NOT NULL,
    odometer_km NUMERIC(10,2),
    notes       TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_maint_expenses_vehicle_id ON maintenance_expenses(vehicle_id);
CREATE INDEX idx_maint_expenses_date ON maintenance_expenses(date);
