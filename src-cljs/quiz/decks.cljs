(ns quiz.decks
  (:require [quiz.utils :as u]
            [quiz.state]
            [ajax.core :refer [GET POST]]
            [quiz.challenge]
            [goog.string :as gstring]
            [goog.string.format]))



(defn start-challenge [deck_id]
  (POST (str js/context "/next-challenge")
        {:headers       {"Accept" "application/transit+json"}
         :params        {:deck_id deck_id}
         :handler       #(quiz.challenge/handle-next-challenge %)
         :error-handler u/error-handler}))

(defn make-decks []

  (map (fn [deck]
         (vector :div.col-md-2.well {:style {:margin "5px" :height 370} :key (:id deck)}
                 [:div {:style {:height 300}}
                  [:img {:style {:paddingBottom "10px"} :width 150 :src (str js/context "/deck-image/" (:id deck))}]
                  [:div (:name deck)]
                  [:div "Cards " (:card_count deck)]
                  (if (not (nil? (:your_rounds deck)))
                    [:div "Your Rounds " [:b (:your_rounds deck)]])
                  (if (not= (:last_round_total deck) 0)
                    (let [prefix (if (= (:last_round_total deck) (:total_challenges deck)) "Last" "Current")]
                      [:div prefix " Round " [:b (gstring/format "%.1f" (/ (* 100 (:last_round_correct deck)) (:last_round_total deck)))] "%"]))
                  (if (not= (:total_challenges deck) 0)
                    [:div "Ever " [:b (gstring/format "%.1f" (/ (* 100 (:correct_challenges deck)) (:total_challenges deck)))] "% "])

                  [:button.btn-primary.btn {:on-click #(start-challenge (:id deck))}
                   (if (= (:is_round_completed deck) "Y") "Start Round" "Continue Round")]]))
       (:decks @quiz.state/app-state)))


(defn decks-page []

  [:div.container
   [:div
    [:h2 "Decks"]]

   (make-decks)])


