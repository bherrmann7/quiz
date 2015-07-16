

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
insert into outcomes ( user, round, name, answer, correct, occured )
values ( :user, :round, :name, :answer, :correct, now() )

-- name: get-current-round-outcomes
select * from outcomes where round = (select max(round) from outcomes where user = :user)

---- name: count-current-outcomes
--select count(1) outcomes_count, IF(max(round) is null,1,max(round)) round, sum(correct) correct_count
--from outcomes where round = (select max(round) from outcomes where user = :user)

-- name:pick-next
SELECT image_name, c.name, c.gender FROM challenges c
LEFT OUTER JOIN (select * from outcomes where user = :user and round = :round) o
ON c.name = o.name where o.name is null
ORDER BY rand() limit 0,1
