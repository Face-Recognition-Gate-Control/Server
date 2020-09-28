\c fractal

CREATE TABLE IF NOT EXISTS users(
    id         uuid primary key    not null,
    firstname  text                not null,
    lastname   text                not null,
    email      text                not null,
    telephone  number              not null,
    password   text                not null,
    created    bigint DEFAULT extract(epoch from now()),

)

CREATE TABLE IF NOT EXISTS stations(
    id             uuid primary key    not null,
    station_name   text                not null,
    last_chekin    bigint              not null,

)

CREATE TABLE IF NOT EXISTS events(
    id             serial int primary key,
    event_name     text                not null,
    event_start    bigint              not null,
    event_end      bigint              not null,
    event_day      bigint              not null,
    repets         boolean             not null,

)
CREATE TABLE IF NOT EXISTS station_events(
    text references stations(id) ON DELETE RESTRICT  not null,
    text references events(id) ON DELETE RESTRICT  not null,

)


CREATE TABLE IF NOT EXISTS roles(
    role_name      text      primary key  not null,
)

CREATE TABLE IF NOT EXISTS user_roles(
    role_name      text references roles(role_name) ON DELETE RESTRICT  not null,
    user_id        text references users(id)        ON DELETE CASCADE   not null,
)

CREATE TABLE IF NOT EXISTS thumbnails(
    user_id        text references users(id)        ON DELETE CASCADE   not null,
    file_name      text                                                 not null,
)

CREATE TABLE IF NOT EXISTS login_referance(
    user_id        uuid references users(id)        ON DELETE CASCADE   not null,
    face_vec       numeric[]                                            not null,
    file_name      text                                                 not null,
)

CREATE TABLE IF NOT EXISTS registrations(
    token          uuid primary key    not null,
    station_name   text                not null,
    expire         bigint              not null,

)

