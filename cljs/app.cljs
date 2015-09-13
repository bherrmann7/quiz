(ns quiz.app
  (:require [reagent.core :as reagent]))

;  (:require [reagent.core :as reagent :refer [atom]]
;            ))
;
;(defonce app-state (atom nil))



(defn home-page []
  [:div
   "Boo"
   ] )

(defn mount-components []
  ;(reagent/render-component [#'navbar] (.getElementById js/document "navbar"))
  (reagent/render-component [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-components))

