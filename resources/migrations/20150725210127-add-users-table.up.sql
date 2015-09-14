CREATE TABLE users
(
   id       INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   email    VARCHAR(30),
   password VARCHAR(300),
   UNIQUE (email)
);
