(ns quiz.core
  (:require [reagent.core :as reagent :refer [atom]]

            [goog.events :as events]
            [ajax.core :refer [GET POST]])
  (:import goog.History))


(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Welcome to quiz"]
    [:p "Time to start building your brain!"]
    ]] )


(defn mount-components []
  (reagent/render home-page (.getElementById js/document "app")))

(defn init! []
  (mount-components))
