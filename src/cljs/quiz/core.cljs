(ns quiz.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [ajax.core :refer [GET POST]]
            [quiz.view :as v]
            [quiz.other :as oth]
            [quiz.utils :as u]))

(defonce app-state (atom {:user "guest"}))

(defn handler [resp]
  (let [message (cljs.reader/read-string resp)
        user (:user message)]
    (if (contains? message :user)
      (swap! app-state assoc :user user)
      (swap! app-state assoc :flash (:message message)))))

(defn error-handler [resp]
  (swap! app-state assoc :flash resp))

(defn do-check-answer [app owner value]
  (swap! app-state conj {:challenge nil :last (assoc (:challenge @app-state) :user-choice value)})
  (ajax.core/POST "/next-challenge"
    {:params {:correct (:name (:last @app-state)) :user-choice value}
     :handler challenge-handler
     :format  :raw}))

(defn challenge-handler [resp]
  (let [c (cljs.reader/read-string resp)]
    (u/l c)
    (swap! app-state assoc :challenge (assoc c :choices (into [] (shuffle (conj (:fakes c) (:name c))))))))


(defn config-handler [resp]
  (let [message (cljs.reader/read-string resp)]
    (swap! app-state assoc :title (:title message))
    ;(do-start-quiz)
    )
  (ajax.core/POST "/next-challenge"
    {:handler challenge-handler
     :format  :raw}))

(defn start-next-round []
  (u/l "requesting start")
  (ajax.core/GET "/start-next-round"
                  {:handler challenge-handler
                   :format  :raw}))


(defn post-fn [path params]
  (ajax.core/POST path
    {:params        params
     :handler       handler
     :error-handler error-handler
     :format        :raw}))

(defn main []
  (om/root
   (fn [app owner]
     (reify
       om/IRender
       (render [_]
         (v/quiz-page app owner do-check-answer))))
   app-state
   {:target (. js/document (getElementById "app"))}))



(ajax.core/POST "/fetch-config"
  {:handler config-handler
   :format  :raw})

