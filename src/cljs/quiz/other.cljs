(ns quiz.other
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [ajax.core :refer [GET POST]]))

(defn loading-quiz [app]

  (dom/div nil
           (dom/h1 nil "Loading " (:title app) " ...")))

(defn my-learning [app]
  (dom/div nil
           (dom/h1 nil "My Learning")
           (dom/br nil)
           ;(dom/input #js {:type "submit" :value "Start Quiz" :onClick q/do-start-quiz})
           ))

