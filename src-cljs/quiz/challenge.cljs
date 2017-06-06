(ns quiz.challenge
  (:require [quiz.utils :as u]
            [quiz.login]
            [quiz.state]
            [goog.string :as gstring]
            [goog.string.format]
            [ajax.core :refer [GET POST]]))


(defn fetch-deck [state deck-id]
  (let [decks (:decks state)]
   (first (filter #(= (:id %) deck-id) decks))))


(defn place-deck [deck-id new-deck]
  (let [
        decks (:decks @quiz.state/app-state)
        other-decks (filter #(not= (:id %) deck-id) decks)
        new-decks (sort-by :id (conj other-decks new-deck))
        new-state (assoc @quiz.state/app-state :decks new-decks)]

    new-state))


(defn update-deck [deck-id props]
  (let [deck (fetch-deck @quiz.state/app-state deck-id)
        is_round_completed (if (= (:card_count deck) (:last_round_total props)) "Y" "N")
        deck-rc (assoc deck :is_round_completed is_round_completed)]

    (u/l "props " props)
    (u/l "deck before " deck)
    (reset! quiz.state/app-state (place-deck deck-id (conj deck-rc props)))
    (u/l "deck after " (fetch-deck @quiz.state/app-state deck-id))))


(defn handle-next-challenge [ch]
  (u/l "ch " ch)
  (update-deck (:deck_id ch)
               { :last_round_correct (:cards_correct ch)
                 :your_rounds (:round ch)
                :last_round_total (:cards_completed ch)})
  (swap! quiz.state/app-state assoc :challenge ch))


(defn choose-color [render-choice correct-choice user-choice]
  (if (or (nil? user-choice) (= "" user-choice))
    "black"
    (if (= render-choice correct-choice)
      "green"
      (if (= render-choice user-choice)
        "red"
        "black"))))

(defn deck-inc-coutner [state deck-id last-answer-correct cards_completed]
  (let [
        deck (fetch-deck state deck-id)
        was_correct_challenges (if (nil? (:correct_challenges deck)) 0 (:correct_challenges deck))]

    (place-deck deck-id  (assoc deck
                           :total_challenges   (inc (:total_challenges deck))
                            :correct_challenges (if last-answer-correct (inc was_correct_challenges) was_correct_challenges)))))



(defn do-check-answer [user-choice]
  (let [challenge (:challenge @quiz.state/app-state)
        deck_id (:deck_id challenge)
        round_id (:round_id challenge)
        correct_card_id (:correct_card_id challenge)
        chosen_id (second (first (filter #(= (first %) user-choice ) (:choices challenge))))
        last (assoc challenge :user-choice user-choice)
        cards_completed (:cards_completed challenge)]
    (swap! quiz.state/app-state assoc :last last :challenge {:loading true})
    (swap! quiz.state/app-state deck-inc-coutner deck_id (= correct_card_id chosen_id) (:round challenge)  cards_completed)
   (POST (str js/context "/next-challenge")
         {:headers       {"Accept" "application/transit+json"}
          :params        {:round_id round_id :deck_id deck_id :card_id correct_card_id :chosen_id chosen_id}
          :handler       #(quiz.challenge/handle-next-challenge  %)
          :error-handler u/error-handler})))



(defn quiz-item-classic [pos choices]
  (if (> pos (count choices)) ""
   (let [render-choice (first (nth choices (dec pos)))
         user-choice nil]
     [:a {:className "choice"
          :style     {:color (choose-color render-choice "correct-choice" user-choice)}
          :onClick   #(do-check-answer render-choice)
          :ref       (str "ans" pos)} nil render-choice])))

(defn quiz-item [pos choices]
  (if (> pos (count choices)) ""
                              (let [render-choice (first (nth choices (dec pos)))
                                    user-choice nil]
                                [:div
                                   [:input {:type "radio" :name "choice" :value (str "ans" pos) :onClick   #(do-check-answer render-choice)}]
                                   "   "
                                   [:a {:className "choice"
                                               :style     {:color (choose-color render-choice "correct-choice" user-choice)}
                                               :onClick   #(do-check-answer render-choice)
                                               :ref       (str "ans" pos) } render-choice]])))



(defn last-item [pos choices correct-choice user-choice]
  (if (> pos (count choices)) ""
   (let [render-choice (first (nth choices (dec pos)))]

     [:div
      [:p]
      [:div {:className "choice" :style {:color (choose-color render-choice correct-choice user-choice)}} render-choice]])))




(defn show-choices [challenge image choices]
  [:div {:style {:float "left" :min-width 300 :width 200 :padding "10px"}}  ;{:display "inline-block"}}
   (if (:answer challenge)
     [:div [:b "Q:"]
      [:div {:style { :padding "0px 0px 0px 20px"}} (:answer challenge) [:br][:br]]]
     [:img {:width 200 :src (str js/context "/card-image/" image)}])
   [:b "A:"]
   [:div {:style { :padding "0px 0px 0px 20px"}}
         [:p]
         (quiz-item 1 choices)
         [:p nil]
         (quiz-item 2 choices)
         [:p nil]
         (quiz-item 3 choices)
         [:p nil]
         (quiz-item 4 choices)
         [:p nil]
         (quiz-item 5 choices)]])


(defn stats [challenge]
  [:div.col-md-3
   [:br nil]
   [:br nil]
   [:table.table [:tr [:td "Completed:"] [:td (:cards_completed challenge)] [:td "Total:"] [:td (:deck_size challenge)]]
          [:tr [:td "Correct:"] [:td (:cards_correct challenge)] [:td "Wrong:"] [:td (- (:cards_completed challenge) (:cards_correct challenge))]]
          [:tr [:td "Round:"] [:td (:round challenge)]]]])



(defn show-last [last]
  (let [{:keys [correct_card_id user-choice]} last
        style-correct { :background-color "lightgrey" :float "left" :margin "0px 0px 0px 40px" :padding "15px"  :width 300 :border "3px solid #B2FFB2"}
        style-with-error {:background-color "lightgrey" :float "left" :margin "0px 0px 0px 40px" :padding "15px"  :width 300 :border "3px solid pink"}
        choices (:choices last)
        correct-choice (ffirst (filter #(= (second %) correct_card_id ) choices))
        style-use (if (= correct-choice user-choice) style-correct style-with-error)]

    [:div {:width 500 :style style-use}
     [:center "Last Round"]
     (if (:answer last)
       [:div (:answer last) [:br][:br][:br]]
       [:img {:width 200 :src (str js/context "/card-image/" correct_card_id  :className "lastimage")}])

     [:br]
     (last-item 1 choices correct-choice user-choice)
     (last-item 2 choices correct-choice user-choice)
     (last-item 3 choices correct-choice user-choice)
     (last-item 4 choices correct-choice user-choice)
     (last-item 5 choices correct-choice user-choice)]))




(defn handle-start-next-round []
  (POST (str js/context "/next-challenge")
        {:headers       {"Accept" "application/transit+json"}
         :params        { :deck_id (:deck_id (:challenge @quiz.state/app-state))}
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
     [:div "Your score was: " [:b (:cards_correct challenge) "/" (:deck_size challenge) " "
                               (gstring/format "%.1f" (/ (* 100 (:cards_correct challenge)) (:deck_size challenge))) "%"]]
     [:br]
     [:input {:type "submit" :value "Next Round"
              :on-click handle-start-next-round}]


     [:br]
     [:br]

     [:input {:type "submit" :value "Return to Decks"
                 :on-click return-to-decks}]]))

(defn challenge-page []
  (let [app-state @quiz.state/app-state
        last (:last app-state)
        image (:correct_card_id (:challenge app-state))
        challenge (:challenge app-state)
        choices (:choices challenge)]

    [:div.container
     [:div.row
      (if (:loading challenge)
        [:div {:style {:float "left" :min-width 300 :width 300}} [:br nil] [:br nil] "Loading..."]
        (if (= (:cards_completed challenge) (:deck_size challenge))
          (completed challenge)
          (show-choices challenge image choices)))

      (if last (show-last last))]

     [:div.row
      (stats challenge)]]))




