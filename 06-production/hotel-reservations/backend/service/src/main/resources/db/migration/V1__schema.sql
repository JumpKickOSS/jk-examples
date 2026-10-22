-- The reservation schema. PostgreSQL 18: uuidv7() keys (time-ordered, index-friendly) and an
-- exclusion constraint that makes a double-booked room impossible, whatever the application does.
-- jOOQ generates its classes from this file without a database (the [jooq] preset interprets it in
-- H2): the parts it cannot model sit between [jooq ignore] markers, and indexed strings are
-- varchar(n) because H2 reads text as a CLOB, which it cannot index.

/* [jooq ignore start] */
CREATE EXTENSION IF NOT EXISTS btree_gist;
/* [jooq ignore stop] */

CREATE TABLE hotel (
    id             uuid PRIMARY KEY /* [jooq ignore start] */ DEFAULT uuidv7() /* [jooq ignore stop] */,
    slug           varchar(64) NOT NULL UNIQUE,
    name           text        NOT NULL,
    city           text        NOT NULL,
    country        text        NOT NULL,
    time_zone      text        NOT NULL,
    stars          smallint    NOT NULL CHECK (stars BETWEEN 1 AND 5),
    tagline        text        NOT NULL,
    description    text        NOT NULL,
    hero_image     text        NOT NULL,
    gallery        text[]      NOT NULL,
    amenities      text[]      NOT NULL,
    check_in_time  time        NOT NULL,
    check_out_time time        NOT NULL,
    currency       char(3)     NOT NULL,
    tax_bp         integer     NOT NULL CHECK (tax_bp BETWEEN 0 AND 5000)
);

CREATE TABLE room_type (
    id              uuid PRIMARY KEY /* [jooq ignore start] */ DEFAULT uuidv7() /* [jooq ignore stop] */,
    hotel_id        uuid        NOT NULL REFERENCES hotel (id),
    code            varchar(8)  NOT NULL,
    name            text        NOT NULL,
    description     text        NOT NULL,
    max_guests      smallint    NOT NULL CHECK (max_guests BETWEEN 1 AND 8),
    beds            text        NOT NULL,
    size_sqm        smallint    NOT NULL,
    image           text        NOT NULL,
    base_rate_minor integer     NOT NULL CHECK (base_rate_minor > 0),
    features        text[]      NOT NULL,
    UNIQUE (hotel_id, code)
);

CREATE TABLE room (
    id           uuid PRIMARY KEY /* [jooq ignore start] */ DEFAULT uuidv7() /* [jooq ignore stop] */,
    hotel_id     uuid      NOT NULL REFERENCES hotel (id),
    room_type_id uuid      NOT NULL REFERENCES room_type (id),
    number       varchar(8) NOT NULL,
    floor        smallint  NOT NULL,
    UNIQUE (hotel_id, number)
);

CREATE TABLE guest (
    id         uuid PRIMARY KEY /* [jooq ignore start] */ DEFAULT uuidv7() /* [jooq ignore stop] */,
    first_name text        NOT NULL,
    last_name  text        NOT NULL,
    email      varchar(254) NOT NULL UNIQUE,
    phone      text        NOT NULL DEFAULT '',
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE reservation (
    id                uuid PRIMARY KEY /* [jooq ignore start] */ DEFAULT uuidv7() /* [jooq ignore stop] */,
    confirmation_code varchar(16) NOT NULL UNIQUE,
    hotel_id          uuid        NOT NULL REFERENCES hotel (id),
    room_id           uuid        NOT NULL REFERENCES room (id),
    guest_id          uuid        NOT NULL REFERENCES guest (id),
    check_in          date        NOT NULL,
    check_out         date        NOT NULL,
    adults            smallint    NOT NULL CHECK (adults >= 1),
    children          smallint    NOT NULL CHECK (children >= 0),
    status            varchar(16) NOT NULL CHECK (status IN ('BOOKED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED')),
    total_minor       bigint      NOT NULL,
    currency          char(3)     NOT NULL,
    special_requests  text        NOT NULL DEFAULT '',
    booked_at         timestamptz NOT NULL DEFAULT now(),
    CHECK (check_out > check_in)
);

CREATE INDEX reservation_hotel_dates ON reservation (hotel_id, check_in, check_out);
CREATE INDEX reservation_guest ON reservation (guest_id);

/* [jooq ignore start] */
-- One room, one live reservation per night: the database refuses an overlapping booking even when
-- two requests race past the application's availability check.
ALTER TABLE reservation ADD CONSTRAINT reservation_no_double_booking
    EXCLUDE USING gist (room_id WITH =, daterange(check_in, check_out, '[)') WITH &&)
    WHERE (status <> 'CANCELLED');
/* [jooq ignore stop] */

CREATE TABLE staff_user (
    id            uuid PRIMARY KEY /* [jooq ignore start] */ DEFAULT uuidv7() /* [jooq ignore stop] */,
    username      varchar(64) NOT NULL UNIQUE,
    display_name  text NOT NULL,
    password_hash text NOT NULL
);

CREATE TABLE staff_hotel (
    staff_id uuid NOT NULL REFERENCES staff_user (id),
    hotel_id uuid NOT NULL REFERENCES hotel (id),
    PRIMARY KEY (staff_id, hotel_id)
);
