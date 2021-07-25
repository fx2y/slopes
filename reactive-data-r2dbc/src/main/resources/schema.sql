DROP TABLE customer;
CREATE TABLE customer
(
    id    SERIAL  NOT NULL PRIMARY KEY,
    email VARCHAR NOT NULL
);