CREATE TABLE tables
(
    id       INT PRIMARY KEY,
    capacity INT NOT NULL default 0
)
    WITH (
        OIDS= FALSE
    );
ALTER TABLE "tables"
    OWNER TO exercise;

CREATE TABLE guests
(
    name               varchar(100) NOT NULL PRIMARY KEY,
    tableNumber        integer      NOT NULL REFERENCES tables (id),
    accompanyingGuests integer      NOT NULL default 0
)
    WITH (
        OIDS= FALSE
    );
ALTER TABLE "guests"
    OWNER TO exercise;

CREATE TABLE arrivals
(
    name                      varchar(100) REFERENCES guests (name),
    arrivedAccompanyingGuests integer NOT NULL default 0
)
    WITH (
        OIDS= FALSE
    );
ALTER TABLE "tables"
    OWNER TO exercise;

INSERT INTO tables(id, capacity) VALUES (1,5), (2,10), (3,15);