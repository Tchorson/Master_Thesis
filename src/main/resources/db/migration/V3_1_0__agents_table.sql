CREATE TABLE IF NOT EXISTS agents
(
    device_name character varying(100) NOT NULL,
    latitude numeric(10,7) NOT NULL,
    longitude numeric(10,7) NOT NULL,
    CONSTRAINT agents_pkey PRIMARY KEY (device_name)
);

