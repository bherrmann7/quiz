CREATE TABLE decks
(
   id         INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name       VARCHAR(80),
   card_count INT,
   image_data BLOB,
   type       varchar(10)
);

-- type is :image, :text
