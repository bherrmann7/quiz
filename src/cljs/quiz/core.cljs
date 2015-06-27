(ns quiz.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [ajax.core :refer [GET POST]]
            ))

(defonce app-state (atom {:user "guest"}))

(defn l [& args]
  (.log js/console (apply str args))
  )

(defn handler [resp]
  (let [message (cljs.reader/read-string resp)
        user (:user message)
        ]
    (if (contains? message :user)
      (swap! app-state assoc :user user)
      (swap! app-state assoc :flash (:message message)))
    )
  )

(defn error-handler [resp]
  (swap! app-state assoc :flash resp)
  )

(defn do-login [owner]
  (let [user-elem (om/get-node owner "username")
        pass-elem (om/get-node owner "password")
        user (.-value user-elem)
        pass (.-value pass-elem)
        ]
    (ajax.core/POST "/send-message"
          {:params        {:user user :password pass}
           :handler       handler
           :error-handler error-handler
           :format        :raw
           }
          )
    ))


(defn login-page [owner]
  (dom/div nil
           (dom/h1 nil (:title @app-state))
           (dom/p nil "Login to get started")
           (dom/br nil)
           (dom/div #js {:style #js {:color "red"}} (:flash @app-state))
           (dom/label #js {:style #js {:width "150px" :display "inline-block"}} nil "username (ie. jsmith)")
           (dom/input #js {:id "username" :type "text" :ref "username" :value "bob"})
           (dom/br nil)
           (dom/label #js { :style #js {:width "150px" :display "inline-block"}} "Password")
           (dom/input #js {:type "password" :ref "password" :value "dk" })
           (dom/br nil)
           (dom/input #js {:type "submit" :value "Login" :onClick #(do-login owner)})
           )
  )

(def peeps [
            ["Tom D" "d10.jpg" :m]
            ["Tim D" "d1.jpg" :m]
            ["Steve D" "d2.jpg" :m]
            ["Allen McD" "d3.jpg" :m]
            ["Harry D" "d4.jpg" :m]
            ["Bob D" "d5.jpg" :m]
            ["Max D" "d6.jpg" :m]
            ["Liam D" "d7.jpg" :m]
            ["William D" "d8.jpg" :m]
            ["Frodo D" "d9.jpg" :m]
            ])

(defn do-start-quiz []
  (let [
        shuf (shuffle peeps)
        [name image gender] (first shuf)
        names (shuffle (take 5 (map #(first %) shuf)))
        ]
    (swap! app-state assoc :challenge [image name names])
    )
  )

(defn do-check-answer [app owner]
  (let [input (om/get-node owner "answer")
        value (.-value input)]
    (set! (.-value input) "")
    (do-check-answer2 app owner value)
    )
  )

(defn do-check-answer2 [app owner value]
    (if (contains? #{"1" "2" "3" "4" "5"} value)
      (let [user-choices (first (rest (rest (:challenge @app-state))))
            user-pos (dec (js/parseInt value))
            user-choice (nth user-choices user-pos)]
        (.log js/console (str "Setting user-choice to " user-choice))
        (swap! app-state assoc :user-choice user-choice)
        (.log js/console (str "Setting user-choice to now: " (:user-choice @app-state)))
        )
      )
    )

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

(defn do-next-round [app owner]
  (let [input (om/get-node owner "answer")
        value (.-value input)]
    (set! (.-value input) "")
    (if (= value "n")
      (do
        (swap! app-state dissoc :user-choice)
        (do-start-quiz)
        )
      )
    )
  )

(defn do-check-answer3 [app owner value]
  (if (empty? (:user-choice @app-state))
    (do-check-answer2 app owner value)
    (do
      (swap! app-state dissoc :user-choice)
      (do-start-quiz)
      )
    )
  )

(defn quiz-item [pos choices app]
  (let [
        render-choice (nth choices (dec pos))
        correct-choice (:name (:challenge app))
        user-choice (:user-choice app)
        ]
    (dom/div #js {:style #js {:color       (choose-color render-choice correct-choice user-choice)
                              :padding-top "8px" :padding-left "10px"

                              }
                :onClick #(do-check-answer3 app owner (str pos))
                  :ref   (str "ans" pos)
                  } nil (str pos " " (nth choices (dec pos))))
    )
  )


(defn quiz-page [app owner]
  (let [c (:challenge app)
        image (:image c)
        choices (into [] (shuffle (conj (:fakes c) (:name c))))
        ]
    (dom/div nil
             (dom/h1 nil (:title @app-state))
             ;      (dom/img #js { :width 200 :height 200 :src image :onKeyDown #(println "idown")  } )
             (dom/img #js {:src (str "/i/" image)})
             (dom/br nil)
             ;(map #(dom/div nil %) choices)
             (quiz-item 1 choices app)
             (quiz-item 2 choices app)
             (quiz-item 3 choices app)
             (quiz-item 4 choices app)
             (quiz-item 5 choices app)
             (dom/br nil)
             (if (contains? app :user-choice)
               (dom/p nil "Next (press N) " (dom/input #js {:ref "answer" :size 1 :onChange #(do-next-round app owner)}) " or click on any name.")
               (dom/p nil "Number? " (dom/input #js {:ref "answer" :size 1 :onChange #(do-check-answer app owner)}) " or click on answer.")
               )
             (dom/br nil)
             (dom/div nil (:total-count c) "/" (:round-size c))
             )
    )
  )

(defn challenge-handler [c]
  (let [message (cljs.reader/read-string c)]
    (l message)
    (swap! app-state assoc :challenge message)
    ))

  (defn loading-quiz [app]

    (dom/div nil
             (dom/h1 nil "Loading " (:title @app-state) " ...")

             )
    )

  (defn my-learning [app]
    (dom/div nil
             (dom/h1 nil "My Learning")
             (dom/br nil)
             (dom/input #js {:type "submit" :value "Start Quiz" :onClick do-start-quiz})
             )
    )

  (defn main []
    (om/root
      (fn [app owner]
        (reify
          om/IRender
          (render [_]
            (if (:challenge app)
              (quiz-page app owner)
              (if (:user app)
                (loading-quiz app)
                (login-page owner)
                )
              )
            )))
      app-state
      {:target (. js/document (getElementById "app"))}))


  (defn config-handler [resp]
    (let [message (cljs.reader/read-string resp)]
      (swap! app-state assoc :title (:title message))
      ;(do-start-quiz)
      )
    (ajax.core/POST "/next-challenge"
                    {:handler challenge-handler
                     :format  :raw
                     }
                    )

    )

  (ajax.core/POST "/fetch-config"
                  {:handler config-handler
                   :format  :raw
                   }
                  )

