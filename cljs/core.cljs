(ns quiz.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]])
  )

(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Wffelcome to broke fig"]
    [:p "Time to start building your site!"]
    [:p [:a.btn.btn-primary.btn-lg {:href "http://luminusweb.net"} "Learn more »"]]]
   [:div.row
    [:div.col-md-12
     [:h2 "Welcome to ClojureScript. +- "]]] ] )


(defn mount-components []
  ;(reagent/render [#'navbar] (.getElementById js/document "navbar"))
  (reagent/render home-page (.getElementById js/document "app")))

(defn init! []
  (mount-components))
