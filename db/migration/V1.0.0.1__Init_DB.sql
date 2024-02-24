CREATE TABLE guests (
                       id integer PRIMARY KEY,
                       name varchar(100) NOT NULL,
                       tableNumber integer NOT NULL default 0,
                       accompanyingGuests bigint NOT NULL default 0,
                       timeArrived TIMESTAMP default NULL
)
    WITH (
        OIDS=FALSE
    );
ALTER TABLE "guests"
    OWNER TO exercise;