(ns quiz.app.main
  (:require quiz.db.core
            [ring.util.response :refer [response status content-type]]))

;
;; We assume the challenges are not updated while we are running.
;;(def cards (quiz.db.core/run quiz.db/cards-query quiz.db/db-spec))
;
;;(def total-cards  (count cards))
;

(defn next-challenge [deck_id round_id card_id chosen_id  {{user_id :user_id} :session}]
  (println "next-challenge " user_id  deck_id round_id card_id chosen_id)

  (if (and deck_id round_id card_id chosen_id)
    (quiz.db.core/insert-outcome! {:deck_id deck_id :user_id user_id :round_id round_id :correct_card_id card_id :chosen_card_id chosen_id :correct (= card_id chosen_id)}
                                  @quiz.db.core/*conn*))

  (let [check-current-round (quiz.db.core/current-round {:user_id user_id :deck_id deck_id} @quiz.db.core/*conn*)
        x (println "check-current-round" check-current-round)
        current-round-raw (if (nil? (:round_id (first check-current-round))) (do
                                                                               (quiz.db.core/insert-round! {:user_id user_id :deck_id deck_id :round 1} @quiz.db.core/*conn*)
                                                                               (quiz.db.core/current-round {:user_id user_id :deck_id deck_id} @quiz.db.core/*conn*))
                              check-current-round)
        round (:round (first current-round-raw))
        current-round (shuffle current-round-raw)
        x (println "c-r" current-round)
        next-correct (first (take 1 (filter #(nil? (:correct %)) current-round)))
        x (println "next-correct" next-correct)]
    (if (and (nil? next-correct) (nil? chosen_id))
      (do
        ; insert new round
        (quiz.db.core/insert-round! {:user_id user_id :deck_id deck_id :round (inc round)} @quiz.db.core/*conn*)
        ; call myself
        (recur deck_id round_id card_id chosen_id {:session {:user_id user_id}}))
      (let [correct-entry [(:name next-correct) (:id next-correct)]
            fake-four (map #(vector (:name %) (:id %)) (take 4 (filter #(not= (:id %) (:id next-correct)) current-round)))
            x (println "fake-four" fake-four)
            choices (shuffle (conj fake-four correct-entry))
            x (println "choices" choices)
            x (println "correct entry" correct-entry)
            message {:deck_id         deck_id
                     :round_id        (:round_id (first current-round))
                     :round           round
                     :correct_card_id (second correct-entry)
                     :choices         choices
                     :deck_size       (count current-round)
                     :cards_completed (count (filter #(not (nil? (:correct %))) current-round))
                     :cards_correct   (count (filter :correct current-round))}]
        (println "------ Should start challenge " message)
        (response message)))))

