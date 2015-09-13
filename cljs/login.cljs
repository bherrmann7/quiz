
(ns quiz.login )

; (:require [quiz.utils :as u]
;           [bouncer.core :as b]
;           [bouncer.validators :as v]
;           [reagent.core :as reagent :refer [atom]]
;           [reagent.session :as session]
;           [ajax.core :refer [GET POST]]
;
;
;           ) )
;
;
;(defonce login-validations (atom nil))
;(defonce login-email (atom nil))
;(defonce login-password (atom nil))
;(defonce registration-success (atom nil))
;(defonce registration-validations (atom nil))
;(defonce registration-email (atom nil))
;(defonce registration-password (atom nil))
;
;
;(defn handler-with-validations [validations register-success response]
;(if (:errors response)
; (reset! validations (:errors response))
; (reset! register-success true)
; (.log js/console (str response))))
;
;
;(defn do-register [validations register-success reg-data]
; (let [attempt-validations (first (b/validate reg-data
;                                              :email v/email
;                                              :password [[v/min-count 4]]))]
;  (reset! validations attempt-validations)
;  (if (nil? attempt-validations)
;   (POST (str js/context "/register")
;         {:headers       {"Accept" "application/transit+json"}
;          :params        reg-data
;          :handler       #(handler-with-validations validations register-success %)
;          :error-handler u/error-handler}))
;  ))
;
;(defn handle-login-response [login-validations resp app-state]
; (u/l "Got " resp)
; ;(.log js/console "should process login response" (:email resp))
; (reset! login-validations (:errors resp))
; (if (:email resp)
;  (reset! app-state resp))
; )
;
;(defn do-login [login-validations reg-data app-state]
; (let [attempt-validations (first (b/validate reg-data
;                                              :email v/email
;                                              :password [[v/min-count 4]]))]
;  (reset! login-validations attempt-validations)
;  (if (nil? attempt-validations)
;   (POST (str js/context "/login")
;         {:headers       {"Accept" "application/transit+json"}
;          :params        reg-data
;          :handler       #(handle-login-response login-validations % app-state)
;          :error-handler u/error-handler}))))
;
;
;(defn login-page [app-state login-validations login-email login-password registration-password registration-validations registration-email registration-success]
;[:div.container
; [:div.jumbotron {:style {:text-align "center"}}
;  [:h1 "Welcome to Quiz"]]
;
; [:div.row
;  [:div.col-md-3
;   [:h2 "Login"]
;
;   [
;    (if (:email @login-validations) :div.form-group.has-error :div.form-group)
;    [:label {:for "login-email"} "Email Address"]
;    [:input.form-control {:id "login-email" :name "email" :placeholder "user@example.com" :value @login-email :on-change #(reset! login-email (-> % .-target .-value))}]
;    [:span.help-block.text-danger (:email @login-validations)]
;    ]
;
;   [
;    (if (:password @login-validations) :div.form-group.has-error :div.form-group)
;    [:label {:for "password"} "Password"]
;    [:input.form-control {:type "password" :name "password" :placeholder "Password" :value @login-password :on-change #(reset! login-password (-> % .-target .-value))}]
;    [:span.help-block.text-danger (:password @login-validations)]
;    ]
;   [:button.btn.btn-default {:type "submit" :on-click #(do-login login-validations {:email @login-email :password @login-password} app-state)} "Login"]
;
;   ]
;  [:div.col-md-1
;   ]
;  [:div.col-md-3
;
;   [:h2 "Register"]
;
;   (if @registration-success
;     [:div.alert.alert-success [:div "Registration Succeeded!"] [:br] [:div "Please login."]]
;     [:div
;
;      [(if (:email @registration-validations) :div.form-group.has-error :div.form-group)
;       [:label {:for "email"} "Email address"]
;       [:input.form-control {:id "email" :placeholder "Email" :value @registration-email :on-change #(reset! registration-email (-> % .-target .-value))}]
;       [:span.help-block.text-danger (:email @registration-validations)]
;       ]
;
;      [(if (:password @registration-validations) :div.form-group.has-error :div.form-group)
;       [:label {:for "password"} "Password"]
;       [:input.form-control {:type "password" :id "password" :placeholder "Password" :value @registration-password :on-change #(reset! registration-password (-> % .-target .-value))}]
;       [:span.help-block.text-danger (:password @registration-validations)]
;       ]
;      [:button.btn.btn-default {:type "submit" :on-click #(do-register registration-validations registration-success {:email @registration-email :password @registration-password})} "Register"]
;      ])]]])