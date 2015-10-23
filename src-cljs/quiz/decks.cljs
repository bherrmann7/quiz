(ns quiz.decks
  (:require [quiz.utils :as u]
            [quiz.state]
            [ajax.core :refer [GET POST]]
            [quiz.challenge]
            [goog.string :as gstring]
            [goog.string.format]
            ))


(defn start-challenge [deck_id]
  (POST (str js/context "/next-challenge")
        {:headers       {"Accept" "application/transit+json"}
         :params        {:deck_id deck_id}
         :handler       #(quiz.challenge/handle-next-challenge  %)
         :error-handler u/error-handler}))

(defn make-decks []

  (map (fn [deck] (vector :div.col-md-2.well {:style {:margin "10px" } :key (:id deck) }
                          [:img { :style { :paddingBottom "10px" } :width 150 :src (str js/context "/deck-image/" (:id deck))}]
                          [:div (:name deck)]
                          [:div "Cards " (/ (:card_count deck) 2)]
                          (if (not= (:total_challenges deck) 0)
                            [:br "Your Rounds " [:b (:your_rounds deck)]] )
                           ; consider showing your "last round" score, not as depressing as Ever
;                          (if (not= (:total_challenges deck) 0)
;                            [:br "Last Round " [:b (gstring/format "%.1f" (/ (* 100 (:correct_challenges deck)) (:total_challenges deck)))] "%"])
                          (if (not= (:total_challenges deck) 0)
                            [:br "Ever " [:b (gstring/format "%.1f" (/ (* 100 (:correct_challenges deck)) (:total_challenges deck)))] "% "]
                            )
                          [:br]
                          [:br]
                          [:button.btn-primary.btn {:on-click #(start-challenge (:id deck))}
                           (if (= (:round_completed deck) 1 ) "Start Round" "Continue Round")]
                          )) (:decks @quiz.state/app-state)))


(defn decks-page []

[:div.container
 [:div
  [:h2 "Decks"]
  ]
 (make-decks)
 ]

)