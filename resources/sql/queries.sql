-- name: create-user!
-- creates a new user record
INSERT INTO users ( email, password) VALUES (:email, :password)

-- name: get-user
-- retrieve a user given the id.
SELECT * FROM users WHERE email = :email

-- name: delete-user!
DELETE FROM users WHERE email = :email

-- name: get-decks
SELECT id, name, card_count, type from decks

-- name: get-deck-image-data
SELECT image_data from decks where id = :id

-- name: insert-deck!
insert into decks values (null, :name, :card_count, :image_data, :type)

-- name: insert-card!
insert into cards values (null, :deck_id, :name, :grouping, 1, :image_data)

-- name: get-card
select * from cards where id = :id

-- name: get-card-image-data
SELECT image_data from cards where id = :id

-- name: delete-outcomes!
delete from outcomes

-- name: delete-rounds!
delete from rounds

-- name: delete-decks!
delete from decks

-- name: delete-cards!
delete from cards

-- name: insert-round!
insert into rounds values (null, :deck_id, :user_id, :round, now(), null )

-- name: close-round!
update rounds set completed_time = now() where deck_id = deck_id and user_id = :user_id and id = :round_id

-- name: insert-outcome!
insert into outcomes ( deck_id, user_id, round_id, correct_card_id, chosen_card_id, correct, occurred )
values ( :deck_id, :user_id, :round_id, :correct_card_id, :chosen_card_id, :correct, now() )

-- name: current-round
select c.id,c.name, r.user_id, r.id round_id, r.round, o.correct, c.answer  from cards c
join rounds r on c.deck_id = r.deck_id and r.user_id = :user_id
left outer join outcomes o on o.round_id = r.id and o.correct_card_id = c.id
where r.id = (select max(id) from rounds where deck_id = :deck_id and user_id = :user_id)

-- name: deck-summary-for-user
SELECT
    d.id,
    name,
    card_count,
    MAX(r.round)                                 your_rounds,
    COUNT(o.deck_id) total_challenges,
    cast(SUM(o.correct) as SIGNED) correct_challenges,
     CASE
        WHEN o.round_id IS NULL
        THEN 'Y'
        WHEN (
                SELECT
                    completed_time
                FROM
                    rounds x
                WHERE
                    x.user_id = r.user_id
                AND x.deck_id = r.deck_id
                ORDER BY
                    ROUND DESC limit 0,1) IS NULL
        THEN 'N'
        ELSE 'Y'
    END is_round_completed,
    cast(sum(case when o.round_id = (select max(round_id) from outcomes where r.user_id = :user_id) then 1 else 0 end) as SIGNED) last_round_total,
    cast(sum(case when o.round_id = (select max(round_id) from outcomes where r.user_id = :user_id) and o.correct = true then 1 else 0 end) as SIGNED) last_round_correct,
    type
FROM
    decks d
LEFT OUTER JOIN
    rounds r
ON
    d.id = r.deck_id and r.user_id = :user_id
LEFT OUTER JOIN
    outcomes o
ON
    r.id = o.round_id
GROUP BY
    d.id
