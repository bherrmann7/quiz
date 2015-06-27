(ns quiz.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [ajax.core :refer [GET POST]]
            [quiz.view :as v]
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


(defn do-check-answer [app owner value]
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




(defn challenge-handler [c]
  (let [message (cljs.reader/read-string c)]
    (l message)
    (swap! app-state assoc :challenge message)
    ))


(defn config-handler [resp]
  (let [message (cljs.reader/read-string resp)]
    (swap! app-state assoc :title (:title message))
    ;(do-start-quiz)
    )
  (ajax.core/POST "/next-challenge"
                  {:handler challenge-handler
                   :format  :raw
                   }
                  ))

(defn post-fn [path params]
  (ajax.core/POST path
                  {:params        params
                   :handler       q/handler
                   :error-handler q/error-handler
                   :format        :raw
                   }
                  )
  )

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (if (:challenge app)
            (v/quiz-page app owner do-check-answer)
            (if (:user app)
              (v/loading-quiz app)
              (v/login-page app owner post-fn)
              )
            )
          )))
    app-state
    {:target (. js/document (getElementById "app"))}))



(ajax.core/POST "/fetch-config"
                {:handler config-handler
                 :format  :raw
                 }
                )

