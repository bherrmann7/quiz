(ns quiz.login
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [ajax.core :refer [GET POST]]))

(defn do-login [owner post-fn]
  (let [user-elem (om/get-node owner "username")
        pass-elem (om/get-node owner "password")
        user (.-value user-elem)
        pass (.-value pass-elem)]
    (post-fn "/send-message" [user pass])))

(defn login-page [app owner post-fn]
  (dom/div nil
           (dom/h1 nil (:title app))
           (dom/p nil "Login to get started")
           (dom/br nil)
           (dom/div #js {:style #js {:color "red"}} (:flash app))
           (dom/label #js {:style #js {:width "150px" :display "inline-block"}} nil "username (ie. jsmith)")
           (dom/input #js {:id "username" :type "text" :ref "username" :value "bob"})
           (dom/br nil)
           (dom/label #js {:style #js {:width "150px" :display "inline-block"}} "Password")
           (dom/input #js {:type "password" :ref "password" :value "dk"})
           (dom/br nil)
           (dom/input #js {:type "submit" :value "Login" :onClick #(do-login owner post-fn)})))
