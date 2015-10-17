(ns quiz.decks
  (:require [quiz.utils :as u]
            [quiz.state]
            [ajax.core :refer [GET POST]]
            [quiz.challenge]
            [goog.string :as gstring]
            [goog.string.format]
            ))


(defn start-challenge [deck_id]
  (u/l "humm")
  (POST (str js/context "/next-challenge")
        {:headers       {"Accept" "application/transit+json"}
         :params        {:deck_id deck_id}
         :handler       #(quiz.challenge/handle-next-challenge  %)
         :error-handler u/error-handler}))

(defn make-decks []

  (map (fn [deck] (vector :div.col-md-2.well {:style {:margin "15px"}}
                          [:img {:src (str js/context "/deck-image/" (:id deck))}]
                          [:div (:name deck)]
                          [:br "Cards " (/ (:card_count deck) 2)]
                          (if (not= (:total_challenges deck) 0)
                            [:br "Your rounds " (:your_rounds deck)] )
                          (if (not= (:total_challenges deck) 0)
                            [:br (gstring/format "%.1f%% correct" (/ (* 100 (:correct_challenges deck)) (:total_challenges deck)))]
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