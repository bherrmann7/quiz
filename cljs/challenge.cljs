(ns quiz.challenge)

;  (:require
;    [ajax.core :refer [GET POST]]
;    [quiz.utils :as u]))
;
;
;(defn handle-next-challenge [app-state event]
;  (u/l "next-challenge " event)
;  (let [new-app-state (assoc @app-state :challenge event)]
;    (reset! app-state new-app-state)
;    )
;  )
;
;(defn start-challenge [app-state deck_id]
;  (POST (str js/context "/next-challenge")
;        {:headers       {"Accept" "application/transit+json"}
;         :params        {:deck_id deck_id}
;         :handler       #(handle-next-challenge app-state %)
;         :error-handler u/error-handler}))
;
;(defn choose-color [render-choice correct-choice user-choice]
;  (if (or (nil? user-choice) (= "" user-choice))
;    "black"
;    (if (= render-choice correct-choice)
;      "green"
;      (if (= render-choice user-choice)
;        "red"
;        "black"))))
;
;;
;(defn do-check-answer [app-state user-choice]
;  (u/l "do-check-answer " )
;  (reset! app-state (assoc app-state :last
;                                    {:image "48" :name "John Smith" :user-choice "Joseph Burmback" :choices ["John Smith" "Barny Adams" "Joseph Burmback" "David Withsmire" "Edward Boohagle"]})
;         ))
;
;(defn quiz-item [pos choices app-state]
;  (let [render-choice (nth choices (dec pos))
;        user-choice nil]
;    [:a {:className "choice"
;         :style     {:color (choose-color render-choice "correct-choice" user-choice)}
;         :onClick   #(do-check-answer app-state render-choice )
;         :ref       (str "ans" pos)} nil render-choice]))
;;
;;(defn last-item [pos choices correct-choice user-choice]
;;  (let [render-choice (nth choices (dec pos))]
;;    [:div  {:className "last" :style    {:color       (choose-color render-choice correct-choice user-choice)
;;                                                  :padding-top "8px" :padding-left "10px"}} nil (str (nth choices (dec pos))))))
;
;
;
;(defn last-item [pos choices correct-choice user-choice]
;  (let [render-choice (nth choices (dec pos))]
;    [:div
;     [:p]
;     [:a {:className "choice" :style {:color (choose-color render-choice correct-choice user-choice)}} render-choice]
;     ]
;    )
;  )
;
;(defn quiz-page [app-state]
;  (u/l "state " @app-state)
;  (u/l "image ")
;
;  (let [last (:last @app-state)
;        image (:correct_card_id (:challenge @app-state))
;        challenge (:challenge @app-state)
;        choices (:choices challenge)
;        choices ["John Smith" "Barny Adams" "Joseph Burmback" "David Withsmire" "Edward Boohagle"]]
;    [:div.container
;     [:h1 "Quiz"]
;
;     (if 1
;       [:div {:style {:float "left" :min-width 300 :width 300}} ;{:display "inline-block"}}
;        [:img {:src (str "/card-image/" image)}]
;        [:p]
;        (quiz-item 1 choices app-state)
;        [:p nil]
;        (quiz-item 2 choices app-state)
;        [:p nil]
;        (quiz-item 3 choices app-state)
;        [:p nil]
;        (quiz-item 4 choices app-state)
;        [:p nil]
;        (quiz-item 5 choices app-state)
;        [:br nil]
;        [:br nil]
;        [:br nil]
;        [:div nil "Completed/Total " (:total-count challenge) "/" (:round-size challenge)]
;        ]
;       )
;     (if last
;       (let [{:keys [choices name user-choice image]} last
;             style-basic {:float "left" :padding "15px"}
;             style-with-error {:float "left" :padding "15px" :border "5px solid pink"}
;             style-use (if (= name user-choice) style-basic style-with-error)]
;         [:div {:width 500 :style style-use}
;          [:img {:src (str "/card-image/" image) :className "lastimage"}]
;          [:br]
;          (last-item 1 choices name user-choice)
;          (last-item 2 choices name user-choice)
;          (last-item 3 choices name user-choice)
;          (last-item 4 choices name user-choice)
;          (last-item 5 choices name user-choice)
;          ]
;         )
;       )
;     ]
;    )
;  )
;
;;(if c
;;  [:div  {:style  {:float "left" :min-width 300 :width 300}} [:br nil) [:br nil)
;;           (dom/h2 nil "End of Round")
;;           [:div nil "Your score was: "  (dom/b nil (:correct_count c) "/" (:total-count c)))
;;           [:br nil)
;;           (dom/input  {:type "submit" :value "Next Round"
;;                           :onClick #(handle-start-next-round)})
;;           ;(pr-str app)
;;           )
;;                 [:div  {:style  {:float "left" :min-width 300 :width 300}} [:br nil) [:br nil) "Loading...")))
;
