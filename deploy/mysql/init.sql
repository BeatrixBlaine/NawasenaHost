-- one database per service, created on the first start of the MySQL container
CREATE DATABASE IF NOT EXISTS nawasenahost_auth;
CREATE DATABASE IF NOT EXISTS nawasenahost_hotel;
CREATE DATABASE IF NOT EXISTS nawasenahost_room;
CREATE DATABASE IF NOT EXISTS nawasenahost_availability;
CREATE DATABASE IF NOT EXISTS nawasenahost_booking;
CREATE DATABASE IF NOT EXISTS nawasenahost_customer;
CREATE DATABASE IF NOT EXISTS nawasenahost_payment;
