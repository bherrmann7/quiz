(ns quiz.view
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [ajax.core :refer [GET POST]]
            [quiz.utils :as u]))


(defn choose-color [render-choice correct-choice user-choice]
  (if (or (nil? user-choice) (= "" user-choice))
    "black"
    (if (= render-choice correct-choice)
      "green"
      (if (= render-choice user-choice)
        "red"
        "black"))))


(defn do-check-answer-click [app owner value do-check-answer]
  (do-check-answer app owner value))

(defn quiz-item [owner pos choices app do-check-answer]
  (let [render-choice (nth choices (dec pos))
        correct-choice (:name (:challenge app))
        user-choice (:user-choice app)]
    (dom/a #js {:className "choice" :style   #js {:color       (choose-color render-choice correct-choice user-choice)}
                :onClick #(do-check-answer-click app owner render-choice do-check-answer)
                :ref     (str "ans" pos)} nil render-choice)))

(defn last-item [pos choices correct-choice user-choice]
  (let [render-choice (nth choices (dec pos))]
    (dom/div #js {:className "last" :style   #js {:color       (choose-color render-choice correct-choice user-choice)
                                                  :padding-top "8px" :padding-left "10px"}} nil (str (nth choices (dec pos))))))


(defn quiz-page [app owner do-check-answer handle-start-next-round]
  (let [c (:challenge app)
        last (:last app)
        image (:image c)
        choices (:choices c)]
    (dom/div nil
             (dom/h1 nil (:title app))
             (if image
               (dom/div #js {:style #js {:float "left" :min-width 300 :width 300}} ;{:display "inline-block"}}
                        (dom/img #js {:src (str "/i/" image)})
                        (dom/p nil)
                        (quiz-item owner 1 choices app do-check-answer)
                        (dom/p nil)
                        (quiz-item owner 2 choices app do-check-answer)
                        (dom/p nil)
                        (quiz-item owner 3 choices app do-check-answer)
                        (dom/p nil)
                        (quiz-item owner 4 choices app do-check-answer)
                        (dom/p nil)
                        (quiz-item owner 5 choices app do-check-answer)
                        (dom/br nil)
                        (dom/br nil)
                        (dom/br nil)
                        (dom/div nil "Completed/Total " (:total-count c) "/" (:round-size c)))
               (if c
                 (dom/div #js {:style #js {:float "left" :min-width 300 :width 300}} (dom/br nil) (dom/br nil)
                          (dom/h2 nil "End of Round")
                          (dom/div nil "Your score was: "  (dom/b nil (:correct_count c) "/" (:total-count c)))
                          (dom/br nil)
                          (dom/input #js {:type "submit" :value "Next Round"
                                          :onClick #(handle-start-next-round)})
                          ;(pr-str app)
                          )
                 (dom/div #js {:style #js {:float "left" :min-width 300 :width 300}} (dom/br nil) (dom/br nil) "Loading...")))
             (if last
               (let [{:keys [choices name user-choice]} last
                     style-basic #js {:float "left" :padding "15px"}
                     style-with-error #js {:float "left" :padding "15px" :border "5px solid pink"}
                     style-use (if (= name user-choice) style-basic style-with-error)]
                 (dom/div #js {:width 500 :style style-use}
                          (dom/img #js {:src (str "/i/" (:image last)) :className "lastimage"})
                          (dom/br nil)
                          (last-item 1 choices name user-choice)
                          (last-item 2 choices name user-choice)
                          (last-item 3 choices name user-choice)
                          (last-item 4 choices name user-choice)
                          (last-item 5 choices name user-choice)))))))
