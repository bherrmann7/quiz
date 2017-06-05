(ns quiz.login

  (:require [quiz.utils :as u]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [reagent.core :as reagent :refer [atom]]
            [quiz.state :as s]
            [ajax.core :refer [GET POST]]))

(defn handler-with-validations [response]
  (if (:errors response)
    (swap! s/app-state assoc :registration-validations (:errors response))
    (do
      (swap! s/app-state assoc :registration-success true)
      (swap! s/app-state assoc :login-email (@s/app-state :registration-email))))

  (.log js/console (str response)))


(defn do-register []
  (let [
        reg-data {:email    (:registration-email @s/app-state)
                  :password (:registration-password @s/app-state)}

        attempt-validations (first (b/validate reg-data
                                               :email v/email
                                               :password [[v/min-count 4]]))]
    (swap! s/app-state assoc :registration-validations attempt-validations)
    (swap! s/app-state dissoc :login-validations)
    (if (nil? attempt-validations)
      (POST (str js/context "/register")
            {:headers       {"Accept" "application/transit+json"}
             :params        reg-data
             :handler       #(handler-with-validations %)
             :error-handler u/error-handler}))))


(defn handle-login-response [resp]
  (js/console.log "handle-login-response called")
  (if (:errors resp)
    (swap! s/app-state assoc :login-validations (:errors resp))
    (reset! s/app-state resp)))

(defn do-login []
  (let [
        reg-data {:email    (:login-email @s/app-state)
                  :password (:login-password @s/app-state)}
        attempt-validations (first (b/validate reg-data
                                               :email v/email
                                               :password [[v/min-count 4]]))]
    (swap! s/app-state assoc :login-validations attempt-validations)
    (swap! s/app-state dissoc :registration-validations)
    (if (nil? attempt-validations)
      (POST (str js/context "/login")
            {:headers       {"Accept" "application/transit+json"}
             :params        reg-data
             :handler       #(handle-login-response %)
             :error-handler u/error-handler}))))

(defn do-login-as-guest []
  (swap! s/app-state dissoc :login-validations)
  (swap! s/app-state dissoc :registration-validations)

  (POST (str js/context "/login-as-guest")
        {:headers       {"Accept" "application/transit+json"}
         :handler       #(handle-login-response %)
         :error-handler u/error-handler}))


(defn update-state [key element]
  (let [value (-> element .-target .-value)]
    (swap! s/app-state assoc key value)))

(defn login-page []
  (let [s @s/app-state
        login-validations (:login-validations s)
        registration-validations (:registration-validations s)
        registration-success (:registration-success s)]

    [:div.container
     [:div.jumbotron {:style {:text-align "center"}}
      [:h1 "Welcome to Quiz"]]

     [:div.row
      [:div.col-md-3
       [:h2 "Login"]

       [
        (if (:email login-validations) :div.form-group.has-error :div.form-group)
        [:label {:for "login-email"} "Email Address"]
        [:input.form-control {:id        "login-email" :name "email" :placeholder "user@example.com"
                              :value     (:login-email s)
                              :on-change #(update-state :login-email %)}]

        [:span.help-block.text-danger (:email login-validations)]]

       [
        (if (:password login-validations) :div.form-group.has-error :div.form-group)
        [:label {:for "password"} "Password"]
        [:input.form-control {:type      "password" :name "password" :placeholder "Password"
                              :value     (:login-password s)
                              :on-change #(update-state :login-password %)}]

        [:span.help-block.text-danger (:password login-validations)]]

       [:button.btn.btn-default {:type     "submit"
                                 :on-click do-login}
                                "Login"]]


      [:div.col-md-1]

      [:div.col-md-3

       [:h2 "Register"]

       (if registration-success
         [:div.alert.alert-success [:div "Registration Succeeded!"] [:br] [:div "Please login."]]
         [:div

          [(if (:email registration-validations) :div.form-group.has-error :div.form-group)
           [:label {:for "email"} "Email address"]
           [:input.form-control {:id    "email" :placeholder "Email"
                                 :value (:registration-email s) :on-change #(update-state :registration-email %)}]

           [:span.help-block.text-danger (:email registration-validations)]]


          [(if (:password registration-validations) :div.form-group.has-error :div.form-group)
           [:label {:for "password"} "Password"]
           [:input.form-control {:type  "password" :id "password" :placeholder "Password"
                                 :value (:registration-password s) :on-change #(update-state :registration-password %)}]

           [:span.help-block.text-danger (:password registration-validations)]]

          [:button.btn.btn-default {:type     "submit"
                                    :on-click do-register} "Register"]])]
      [:div.col-md-1]
      [:div.col-md-3
       [:h2 "Guest"]

       [:div "We can not save your progress if you login as guest."]
       [:br]

       [:button.btn.btn-default {:type     "submit"
                                  :on-click do-login-as-guest} "Login as Guest"]]]]))

