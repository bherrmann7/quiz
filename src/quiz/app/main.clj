(ns quiz.app.main
  (:require quiz.db.core
            [ring.util.response :refer [response status content-type]]
            [environ.core :refer [env]]))

(defn next-challenge
  "Used to get the next challenge.   Can be invoked with the results of the last challenge"
  [deck_id round_id card_id chosen_id {{user_id :user_id} :session}]

  ; If the player called us with an outcome, save that.
  (if (and deck_id round_id card_id chosen_id)
    (quiz.db.core/insert-outcome! {:deck_id deck_id :user_id user_id :round_id round_id :correct_card_id card_id :chosen_card_id chosen_id :correct (= card_id chosen_id)}
                                  @quiz.db.core/*conn*))

  ; get current round.   If very first time, insert new round record
  (let [cards-in-round-maybe (quiz.db.core/cards-in-current-round {:user_id user_id :deck_id deck_id} @quiz.db.core/*conn*)
        cards (if (nil? (:round_id (first cards-in-round-maybe))) (do
                                                                    (quiz.db.core/insert-round! {:user_id user_id :deck_id deck_id :round 1} @quiz.db.core/*conn*)
                                                                    (quiz.db.core/cards-in-current-round {:user_id user_id :deck_id deck_id} @quiz.db.core/*conn*))
                  cards-in-round-maybe)
        round (:round (first cards))
        cards-shuffled (shuffle cards)
        next-correct (first (take 1 (filter #(nil? (:correct %)) cards-shuffled)))]
    ; if there is no next card, and the user isnt providing a correct card
    (if (and (nil? next-correct) (nil? chosen_id))
      (do
        ; insert new round
        (quiz.db.core/insert-round! {:user_id user_id :deck_id deck_id :round (inc round)} @quiz.db.core/*conn*)
        ; call myself to lookup all cards now.
        (recur deck_id round_id card_id chosen_id {:session {:user_id user_id}}))

      (let [correct-entry [(:name next-correct) (:id next-correct)]
            correct-group (:grouping next-correct)
            fake-four (map #(vector (:name %) (:id %)) (take 4 (filter #(and (= (:grouping %) correct-group) (not= (:id %) (:id next-correct))) cards-shuffled)))
            choices (shuffle (conj fake-four correct-entry))
            message {:deck_id         deck_id
                     :round_id        (:round_id (first cards-shuffled))
                     :round           round
                     :correct_card_id (second correct-entry)
                     :choices         choices
                     :deck_size       (count cards-shuffled)
                     :cards_completed (count (filter #(not (nil? (:correct %))) cards-shuffled))
                     :cards_correct   (count (filter :correct cards-shuffled))
                     :answer          (:answer next-correct)}]
        (if (nil? (second correct-entry))
          (quiz.db.core/close-round! {:user_id user_id :deck_id deck_id :round_id round_id} @quiz.db.core/*conn*))
        (response message)))))

