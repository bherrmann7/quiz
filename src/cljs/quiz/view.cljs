(ns quiz.view
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [ajax.core :refer [GET POST]]
            [quiz.core :as q]
            ))


(defn choose-color [render-choice correct-choice user-choice]
  ;    (.log js/console render-choice correct-choice user-choice  )
  (if (or (= nil user-choice) (= "" user-choice))
    "black"
    (if (= render-choice correct-choice)
      "green"
      (if (= render-choice user-choice)
        "red"
        "black"))
    )
  )

(defn do-login [owner]
  (let [user-elem (om/get-node owner "username")
        pass-elem (om/get-node owner "password")
        user (.-value user-elem)
        pass (.-value pass-elem)
        ]
    (ajax.core/POST "/send-message"
                    {:params        {:user user :password pass}
                     :handler       q/handler
                     :error-handler q/error-handler
                     :format        :raw
                     }
                    )
    ))

(defn login-page [owner]
  (dom/div nil
           (dom/h1 nil (:title @q/app-state))
           (dom/p nil "Login to get started")
           (dom/br nil)
           (dom/div #js {:style #js {:color "red"}} (:flash @q/app-state))
           (dom/label #js {:style #js {:width "150px" :display "inline-block"}} nil "username (ie. jsmith)")
           (dom/input #js {:id "username" :type "text" :ref "username" :value "bob"})
           (dom/br nil)
           (dom/label #js {:style #js {:width "150px" :display "inline-block"}} "Password")
           (dom/input #js {:type "password" :ref "password" :value "dk"})
           (dom/br nil)
           (dom/input #js {:type "submit" :value "Login" :onClick #(do-login owner)})
           )
  )


(defn quiz-item [owner pos choices app]
  (let [
        render-choice (nth choices (dec pos))
        correct-choice (:name (:challenge app))
        user-choice (:user-choice app)
        ]
    (dom/div #js {:style   #js {:color       (choose-color render-choice correct-choice user-choice)
                                :padding-top "8px" :padding-left "10px"

                                }
                  :onClick #(q/do-check-answer3 app owner (str pos))
                  :ref     (str "ans" pos)
                  } nil (str pos " " (nth choices (dec pos))))
    )
  )

(defn quiz-page [app owner]
  (let [c (:challenge app)
        image (:image c)
        choices (into [] (shuffle (conj (:fakes c) (:name c))))
        ]
    (dom/div nil
             (dom/h1 nil (:title @q/app-state))
             ;      (dom/img #js { :width 200 :height 200 :src image :onKeyDown #(println "idown")  } )
             (dom/img #js {:src (str "/i/" image)})
             (dom/br nil)
             ;(map #(dom/div nil %) choices)
             (quiz-item owner 1 choices app)
             (quiz-item owner 2 choices app)
             (quiz-item owner 3 choices app)
             (quiz-item owner 4 choices app)
             (quiz-item owner 5 choices app)
             (dom/br nil)
             (if (contains? app :user-choice)
               (dom/p nil "Next (press N) " (dom/input #js {:ref "answer" :size 1 :onChange #(q/do-next-round app owner)}) " or click on any name.")
               (dom/p nil "Number? " (dom/input #js {:ref "answer" :size 1 :onChange #(q/do-check-answer app owner)}) " or click on answer.")
               )
             (dom/br nil)
             (dom/div nil (:total-count c) "/" (:round-size c))
             )
    )
  )

(defn loading-quiz [app]

  (dom/div nil
           (dom/h1 nil "Loading " (:title @q/app-state) " ...")

           )
  )

(defn my-learning [app]
  (dom/div nil
           (dom/h1 nil "My Learning")
           (dom/br nil)
           (dom/input #js {:type "submit" :value "Start Quiz" :onClick q/do-start-quiz})
           )
  )