DROP TABLE IF EXISTS user_routes_daily;
DROP TABLE IF EXISTS user_routes_history;
DROP TABLE IF EXISTS user_registrations;
DROP TABLE IF EXISTS fugitives;

CREATE TABLE IF NOT EXISTS user_registrations
(
    user_id character varying(15) COLLATE pg_catalog."default" NOT NULL,
    walk_date bigint NOT NULL,
    latitude numeric(10,7) NOT NULL,
    longitude numeric(10,7) NOT NULL,
    approved boolean,
    CONSTRAINT user_registrations_pkey PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS user_routes_daily
(
    user_id character varying(15) COLLATE pg_catalog."default" NOT NULL,
    device_id character varying(40) COLLATE pg_catalog."default" NOT NULL,
    "timestamp" bigint NOT NULL
);

CREATE TABLE IF NOT EXISTS public.user_routes_history
(
    user_id character varying(15) COLLATE pg_catalog."default",
    device_id character varying(20) COLLATE pg_catalog."default",
    "timestamp" bigint
);

CREATE TABLE IF NOT EXISTS public.fugitives
(
    user_id character varying(15) COLLATE pg_catalog."default" NOT NULL,
    latitude numeric(10,7),
    longitude numeric(10,7),
    escape_date bigint NOT NULL,
    CONSTRAINT fugitives_pk PRIMARY KEY (user_id)
);
