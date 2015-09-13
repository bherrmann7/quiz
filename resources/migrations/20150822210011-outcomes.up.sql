 create table outcomes (
    deck_id int not null,
    user_id int not null,
    round_id int not null,
    correct_card_id int not null,
    chosen_card_id int not null,
    correct bit(1),
    occurred datetime);