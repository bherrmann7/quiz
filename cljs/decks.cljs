(ns quiz.decks)

;
;
;(defn make-decks [app-state start-challenge]
;
;  (map (fn [deck] (vector :div.col-md-2.well {:style {:margin "15px"}}
;                          [:img {:src (str js/context "/deck-image/" (:id deck))}]
;                          [:div (:name deck)]
;                          [:br "Cards " (/ (:card_count deck) 2)]
;                          [:br "Your rounds 0"]
;                          [:br "Your % correct N/A"]
;                          [:br]
;                          [:br]
;                          [:button.btn-primary.btn {:on-click #(start-challenge app-state (:id deck))} "Start Round"]
;                          )) (:decks @app-state)))
;
;
;(defn decks-page [app-state start-challenge]
;
;[:div.container
; [:div
;  [:h2 "Decks"]
;  ]
; (make-decks app-state start-challenge )
; ]
;
;)