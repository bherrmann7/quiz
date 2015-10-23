(ns quiz.challenge
  (:require [quiz.utils :as u]
            [quiz.login]
            [quiz.state]
            [ajax.core :refer [GET POST]]))

(defn handle-next-challenge [response]
  (swap! quiz.state/app-state assoc :challenge response)
  )

(defn choose-color [render-choice correct-choice user-choice]
  (if (or (nil? user-choice) (= "" user-choice))
    "black"
    (if (= render-choice correct-choice)
      "green"
      (if (= render-choice user-choice)
        "red"
        "black"))))

(defn deck-inc-coutner [state deck-id last-answer-correct]
  (let [decks (:decks state)
        deck (first (filter #(= (:id %) deck-id) decks))
        other-decks (filter #(not= (:id %) deck-id) decks)
        total_challenges (inc (:total_challenges deck))
        was_correct_challenges (:correct_challenges deck)
        correct_challenges (if last-answer-correct (inc was_correct_challenges) was_correct_challenges)
        new-deck (assoc deck :total_challenges total_challenges :correct_challenges correct_challenges)
        new-decks (sort-by :id (conj other-decks new-deck))
        new-state (assoc state :decks new-decks)
  ]
  new-state
  ))

(defn do-check-answer [user-choice]
  (let [challenge (:challenge @quiz.state/app-state)
        deck_id (:deck_id challenge)
        round_id (:round_id challenge)
        correct_card_id (:correct_card_id challenge)
        chosen_id (second (first (filter #(= (first %) user-choice ) (:choices challenge ) )))
        last (assoc challenge :user-choice user-choice)]
    (swap! quiz.state/app-state assoc :last last :challenge {:loading true})
    (swap! quiz.state/app-state deck-inc-coutner deck_id (= correct_card_id chosen_id))
  (POST (str js/context "/next-challenge")
        {:headers       {"Accept" "application/transit+json"}
         :params        {:round_id round_id :deck_id deck_id :card_id correct_card_id :chosen_id chosen_id }
         :handler       #(quiz.challenge/handle-next-challenge  %)
         :error-handler u/error-handler}))
)


(defn quiz-item [pos choices]
  (let [render-choice (first (nth choices (dec pos)))
        user-choice nil]
    [:a {:className "choice"
         :style     {:color (choose-color render-choice "correct-choice" user-choice)}
         :onClick   #(do-check-answer render-choice)
         :ref       (str "ans" pos)} nil render-choice]))

(defn last-item [pos choices correct-choice user-choice]
  (let [render-choice (first (nth choices (dec pos)))
        ]
    [:div
     [:p]
     [:div {:className "choice" :style {:color (choose-color render-choice correct-choice user-choice)}} render-choice]
     ]
    )
  )

(defn show-choices [challenge image choices]
  [:div {:style {:float "left" :min-width 300 :width 200}}  ;{:display "inline-block"}}
   [:img {:width 200 :src (str js/context "/card-image/" image)}]
   [:p]
   (quiz-item 1 choices)
   [:p nil]
   (quiz-item 2 choices)
   [:p nil]
   (quiz-item 3 choices)
   [:p nil]
   (quiz-item 4 choices)
   [:p nil]
   (quiz-item 5 choices)
   [:br nil]
   [:br nil]
   [:br nil]
   [:div nil "Completed/Total " (:cards_completed challenge) "/" (:deck_size challenge) ]
   [:div nil "Correct/Wrong " (:cards_correct challenge) "/"  (- (:cards_completed challenge) (:cards_correct challenge)) ]
   [:div " Round " (:round challenge) ]
   ]
  )

(defn show-last [last]
  (let [{:keys [correct_card_id user-choice]} last
        style-correct {:float "left" :padding "15px" :border "2px solid #B2FFB2"}
        style-with-error {:float "left" :padding "15px" :border "2px solid pink"}
        choices (:choices last)
        correct-choice (ffirst (filter #(= (second %) correct_card_id ) choices ))
        style-use (if (= correct-choice user-choice) style-correct style-with-error)
        ]
    [:div {:width 500 :style style-use}
     [:img {:width 200 :src (str js/context "/card-image/" correct_card_id) :className "lastimage"}]
     [:br]
     (last-item 1 choices correct-choice user-choice)
     (last-item 2 choices correct-choice user-choice)
     (last-item 3 choices correct-choice user-choice)
     (last-item 4 choices correct-choice user-choice)
     (last-item 5 choices correct-choice user-choice)
     ]
    )
  )

(defn handle-start-next-round []
  (POST (str js/context "/next-challenge")
        {:headers       {"Accept" "application/transit+json"}
         :params        { :deck_id (:deck_id (:challenge @quiz.state/app-state))  }
         :handler       #(quiz.challenge/handle-next-challenge  %)
         :error-handler u/error-handler}))

(defn return-to-decks []
  (POST (str js/context "/decks")
        {:headers       {"Accept" "application/transit+json"}
         :handler       #(quiz.login/handle-login-response  %)
         :error-handler u/error-handler}))

(defn completed [challenge]
  (if (= (:cards_completed challenge) (:deck_size challenge))
    [:div {:style {:float "left" :min-width 300 :width 300}}
     [:br]
     [:br]
     [:h2 "End of Round " (:round challenge)]
     [:div "Your score was: " [:b (:cards_correct challenge) "/" (:deck_size challenge)]]
     [:br]
     [:input {:type "submit" :value "Next Round"
              :on-click handle-start-next-round
              }
      ]
     [:br]
     [:br]

     [:input {:type "submit" :value "Return to Decks"
                 :on-click return-to-decks
                 }
         ]
     ]
    ))

(defn challenge-page []

  (let [
        app-state @quiz.state/app-state
        last (:last app-state)
        image (:correct_card_id (:challenge app-state))
        challenge (:challenge app-state)
        choices (:choices challenge)
        ]
    [:div.container

     (if (:loading challenge)
       [:div {:style {:float "left" :min-width 300 :width 300}} [:br nil] [:br nil] "Loading..."]
       (if (= (:cards_completed challenge) (:deck_size challenge))
         (completed challenge)
         (show-choices challenge image choices)))

     (if last (show-last last))
     ]
    )
  )
