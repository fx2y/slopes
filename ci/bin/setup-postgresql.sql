CREATE DATABASE orders;
CREATE USER orders with password 'orders';
grant all privileges on DATABASE orders to orders;
create table customer
(
    id    serial primary key,
    email varchar(255) not null
);