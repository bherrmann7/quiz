(ns quiz.routes.home
  (:require [quiz.layout :as layout]
            [compojure.core :refer [defroutes POST GET]]
            [ring.util.http-response :refer [ok]]
            [quiz.db.core]
            [ring.util.response :refer [response status content-type]]
            [clojure.java.io :as io]
            [buddy.hashers :as hashers])
  (:use  [quiz.app.main])
  (:import (java.sql BatchUpdateException)))

(defn home-page []
  (layout/render "home.html"))

(defn hash-password [req-map]
  (assoc req-map :password (hashers/encrypt (:password req-map))))

(defn register-request [req]
  (println "--- got request a " (:params req))
  (println " selected " (select-keys  (:params req) [:email :password]))
  (let [x (try
            (quiz.db.core/create-user! (hash-password (select-keys (:params req) [:email :password])) @quiz.db.core/*conn*)
            (catch BatchUpdateException e
              (println (.getMessage e))
              (if (.contains (.getMessage e) "@")
                (response {:errors {:email "Email already in use"}}))))]
    (if (= x 1)
      (response "Registered")
      x)
    ;(response {:errors {:username "Username already in use." :email "Email address already registered" :password "too short"}})
))

(defn login-summary [user-id]
  {:user_id user-id
   :decks (quiz.db.core/get-decks @quiz.db.core/*conn*)
   :stats [{:deck_id 1
            :completed_rounds 5
            :cards_completed 3
            :last-complete-round-percent 75}]})

(defn login-req [email password {session :session}]
  (let [some-user (first (quiz.db.core/get-user {:email email} @quiz.db.core/*conn*))]
    (if some-user
      (if (hashers/check password (:password some-user))
        (assoc (response (login-summary (:id some-user))) :session (assoc session :user_id (:id some-user)))
        (response {:errors {:password "Incorrect Password"}}))
      (response {:errors {:email "Not found"}}))))

(defn send-card-image [id]
  (content-type {:status 200
                 :body   (clojure.java.io/input-stream (:image_data (first (quiz.db.core/get-card-image-data {:id id} @quiz.db.core/*conn*))))} "image/png"))

(defn send-deck-image [id]
  (content-type {:status 200
;                 :body   (clojure.java.io/input-stream (:image_data (first (quiz.db.core/run quiz.db.core/get-deck-image-data {:id id}))))} "image/png"))
                 :body   (clojure.java.io/input-stream (:image_data (first (quiz.db.core/get-deck-image-data {:id id} @quiz.db.core/*conn*))))} "image/png"))

(defn decks [{{user_id :user_id} :session}]
  (response (login-summary user_id)))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/card-image/:id" [id] (send-card-image id))
  (GET "/deck-image/:id" [id] (send-deck-image id))
  (POST "/decks" req (decks req))
  (POST "/register" req (register-request req))
  (POST "/login" [email password :as req] (login-req email password req))
  (POST "/next-challenge" [deck_id round_id card_id chosen_id :as req] (next-challenge deck_id round_id  card_id chosen_id req)))

