CREATE TABLE user (
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,
    username        VARCHAR(128) NOT NULL,
    name            VARCHAR(128) NOT NULL,
    email           VARCHAR(128) NOT NULL,
    passwordHash    VARCHAR(128) NOT NULL
);