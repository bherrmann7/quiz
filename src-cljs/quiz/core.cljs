(ns quiz.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [markdown.core :refer [md->html]]
            [quiz.state]
            [quiz.login]
            [quiz.decks]
            [ajax.core :refer [GET POST]])
  (:import goog.History))


(defn route []
  (if (:challenge @quiz.state/app-state)
  (quiz.challenge/challenge-page)
    (if (:decks @quiz.state/app-state)
      (quiz.decks/decks-page)
      (quiz.login/login-page)
      )))

(defn mount-components []
  (reagent/render [route] (.getElementById js/document "app")))

(defn init! []
  (mount-components))
