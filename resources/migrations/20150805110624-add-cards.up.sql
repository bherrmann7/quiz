CREATE TABLE cards
(
   id         INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   deck_id    int not null,
   name       VARCHAR(80),
   grouping   VARCHAR(20),
   enabled    bit(1),
   image_data MEDIUMBLOB
);

