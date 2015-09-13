
 create table rounds (
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   deck_id int not null,
   user_id int not null,
   round int,
   start_time datetime,
   completed_time datetime);
