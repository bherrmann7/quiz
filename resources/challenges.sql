

-- name: challenges-query
-- loads challenges
SELECT *
FROM challenges

-- name: add-challenge!
insert into challenges( image_name, name, gender )
values ( :image_name, :name, "M" )

-- name: delete-challenges!
delete from challenges

-- name: insert-outcome!
insert into outcomes ( user, round, image_name, answer, correct, moment )
values ( :user, :round, :image_name, :answer, :correct, :moment )

-- name: get-current-round-outcomes
select * from outcomes where round = (select max(round) from outcomes where user = :user)

