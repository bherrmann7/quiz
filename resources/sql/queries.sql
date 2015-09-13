-- name: create-user!
-- creates a new user record
INSERT INTO users
( email, password)
VALUES (:email, :password)

-- name: get-user
-- retrieve a user given the id.
SELECT * FROM users
WHERE email = :email

-- name: delete-user!
-- delete a user given the id
DELETE FROM users
WHERE email = :email

-- name: get-decks
SELECT id, name, card_count from decks

-- name: get-deck-image-data
SELECT image_data from decks where id = :id

-- name: insert-deck!
insert into decks values (null, :name, :card_count, :image_data)

-- name: insert-card!
insert into cards values (null, :deck_id, :name, :grouping, 1, :image_data)

-- name: get-card
select * from cards where id = :id

-- name: get-card-image-data
SELECT image_data from cards where id = :id


-- name: delete-decks!
delete from decks

-- name: delete-cards!
delete from cards

-- name: insert-round!
insert into rounds values (null, :deck_id, :user_id, :round, now(), null )

-- name: insert-outcome!
insert into outcomes ( deck_id, user_id, round_id, correct_card_id, chosen_card_id, correct, occurred )
values ( :deck_id, :user_id, :round_id, :correct_card_id, :chosen_card_id, :correct, now() )


-- name: current-round
select c.id,c.name, r.user_id, r.id round_id, r.round, o.correct  from cards c
join rounds r on c.deck_id = r.deck_id and r.user_id = :user_id
left outer join outcomes o on o.round_id = r.id and o.correct_card_id = c.id
where r.id = (select max(id) from rounds where deck_id = :deck_id and user_id = :user_id)
