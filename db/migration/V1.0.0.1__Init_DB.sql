CREATE TABLE IF NOT EXISTS tables
(
    id       INT PRIMARY KEY,
    capacity INT NOT NULL DEFAULT 0
);

ALTER TABLE IF EXISTS tables
    OWNER TO exercise;

CREATE TABLE IF NOT EXISTS guests
(
    name         varchar(255) NOT NULL PRIMARY KEY,
    table_number INT          NOT NULL REFERENCES tables (id),
    total_guests INT          NOT NULL default 1
);

ALTER TABLE IF EXISTS guests
    OWNER TO exercise;

CREATE TABLE IF NOT EXISTS arrived_guests
(
    name  varchar(255) REFERENCES guests (name),
    count integer NOT NULL default 1,
    time_arrived TIMESTAMP NOT NULL default NOW()
);

ALTER TABLE IF EXISTS arrived_guests
    OWNER TO exercise;

-- Tables capacity initialization.
INSERT INTO tables(id, capacity)
VALUES (1, 5),
       (2, 10),
       (3, 12)
ON CONFLICT DO NOTHING;