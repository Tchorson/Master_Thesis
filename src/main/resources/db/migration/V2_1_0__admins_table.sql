CREATE TABLE IF NOT EXISTS admins
(
    login character varying(25) NOT NULL,
    password character varying(100) NOT NULL,
    token character varying(30) NULL,
    timestamp bigint NULL,
    CONSTRAINT admins_pkey PRIMARY KEY (login)
);

