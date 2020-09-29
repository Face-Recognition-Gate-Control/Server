\c fractal

CREATE TABLE IF NOT EXISTS updates_table(
   id             text primary key    not null,
   last_change    bigint              not null
);


CREATE FUNCTION watch_updates(tbl text) RETURNS void AS $$
    BEGIN
        INSERT INTO
            updates_table (id, last_change)
            VALUES (tbl, extract(epoch from now()))
            ON CONFLICT (id)
            DO
                UPDATE SET (last_change) = extract(epoch from now());
    END;
    $$ LANGUAGE plpgsql;

CREATE FUNCTION watch_user_data() RETURNS TRIGGER AS $$
    BEGIN
    EXECUTE watch_updates('new_user_queue');
    END;
    $$ LANGUAGE plpgsql;

-- table with users
CREATE TABLE IF NOT EXISTS users(
    id         uuid primary key    not null,
    firstname  text                not null,
    lastname   text                not null,
    email      text                not null,
    telephone  int                 not null,
    password   text                not null,
    created    bigint DEFAULT extract(epoch from now())

);

-- table with stations
CREATE TABLE IF NOT EXISTS stations(
    id             uuid primary key    not null,
    login_key      text                not null,
    station_name   text                not null,
    last_chekin    bigint              not null

);

-- when what user entered what station
CREATE TABLE IF NOT EXISTS user_enter_events(
   user_id        uuid references users(id)   not null,
   station_id     uuid references stations(id)   not null,
   enter_time     bigint DEFAULT extract(epoch from now())
);

-- the timetable for stuff this may change per frontend result
CREATE TABLE IF NOT EXISTS events(
    id             serial primary key,
    event_name     text                not null,
    event_start    bigint              not null,
    event_end      bigint              not null,
    event_day      bigint              not null,
    repets         boolean             not null

);

-- the stations the timetable events are on
CREATE TABLE IF NOT EXISTS station_events(
    station_id uuid references stations(id) ON DELETE RESTRICT  not null,
    event_id   int references events(id) ON DELETE RESTRICT  not null

);


CREATE TABLE IF NOT EXISTS roles(
    role_name      text      primary key  not null
);

CREATE TABLE IF NOT EXISTS user_roles(
    role_name      text references roles(role_name) ON DELETE RESTRICT  not null,
    user_id        uuid references users(id)        ON DELETE CASCADE   not null
);

CREATE TABLE IF NOT EXISTS thumbnails(
    user_id        uuid references users(id)        ON DELETE CASCADE   not null,
    file_name      text                                                 not null
);

CREATE TABLE IF NOT EXISTS login_referance(
    user_id        uuid references users(id)        ON DELETE CASCADE   not null,
    face_vec       numeric[]                                            not null,
    file_name      text                                                 not null
);


CREATE TABLE IF NOT EXISTS new_user_queue(
    tmp_id         uuid        not null,
    face_vec       numeric[]   not null,
    station_id     uuid references stations(id),
    file_name      text        ,
    added_ts       bigint DEFAULT extract(epoch from now())
);

CREATE TABLE IF NOT EXISTS registrations(
    token          uuid primary key    not null,
    station_name   text                not null,
    expire         bigint              not null

);


CREATE TRIGGER watch_user_data
    AFTER INSERT OR DELETE OR UPDATE
    ON login_referance
    EXECUTE FUNCTION watch_user_data();


GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO fractal;