(ns quiz.server
  (:import (java.util Date))
  (:require [clojure.java.io :as io]
            [quiz.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [site]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [ring.middleware.reload :as reload]
            [environ.core :refer [env]]
            [quiz.db]
            [quiz.config]
            [ring.adapter.jetty :refer [run-jetty]]))
(import [java.io ByteArrayInputStream ByteArrayOutputStream])

(def home-dir (System/getProperty "user.home"))

(deftemplate page
  (io/resource "index.html") [] [:body] (if is-dev? inject-devmode-html identity))

(def challenges (quiz.db/challenges-query quiz.db/db-spec))

(def total-challenges
  (count challenges))

(defn next-challenge [req])

(defn post [req]
  (let [user (:user (:params req))
        pass (:password (:params req))]
    (if (= "bob" user)
      (if (= "dk" pass)
        {:status 200 :body (pr-str {:user user}) :session {:user user}}
        ;(next-challenge req {:user user})
        {:status 200 :body (pr-str {:message "Bad Password"})})
      {:status 200 :body (pr-str {:message "Bad Username"})})))

(defn data-page [req]
  (map #(str "<div style='display:inline-block;margin: 5px;margin-top: 20px;text-align: center'><img width=300px src='/i/" (:image_name %) "'><br>" (:name %) " </div>") challenges))

(defn serv-image [image]
  {:status 200
   :body   (new java.io.FileInputStream (str home-dir "/shapes/" (java.net.URLDecoder/decode image)))})

(defn extract-round [outcomes]
  (if (empty? outcomes)
    1
    (:round (first outcomes))))

(defn record-and-fetch-outcomes [user correct answer]
  (let [{:keys [outcomes_count round]} (first (quiz.db/count-current-outcomes quiz.db/db-spec user))]
    (if (empty? answer)
      [outcomes_count round]
      (do
        (quiz.db/insert-outcome! quiz.db/db-spec user round correct answer (= correct answer))
        [(inc outcomes_count) round]))))

(defn pick-next [user  current-round]
  (quiz.db/pick-next quiz.db/db-spec user current-round))

(defn pick-fakes [name gender]
  (map :name (take 4 (shuffle (filter #(not (= name (:name %))) challenges)))))

(defn generate-next-challenge [user correct answer]
  (let [[outcomes-count round] (record-and-fetch-outcomes user correct answer)
        [{:keys [image_name name gender]}] (pick-next user round)
        message {:image       image_name
                 :name        name
                 :gender      gender
                 :round-size   total-challenges
                 :total-count  (:outcomes_count (first (quiz.db/count-current-outcomes quiz.db/db-spec user)))
                 :fakes       (pick-fakes name gender)}]
    (println "sending -- " message)
    message))

(defn next-challenge [req]
  (let [;new-session (merge (:session req) )
        user-choice (get (:form-params req) "user-choice")
        correct  (get (:form-params req) "correct")]
    {:status  200
     ;   :body (pr-str (generate-next-challenge (:user new-session) (:image req ) (:answer req) ))
     :body    (pr-str (generate-next-challenge "guest" correct user-choice)) ;"41_150.gif" "Bill Clinton"))
     ;:session new-session
     }))

(defn fetch-config [req]
  {:status  200
   :body   (pr-str quiz.config/config)})

(defroutes routes
  (resources "/")
  (resources "/react" {:root "react"})
  (GET "/next-challenge" req (next-challenge req))
  (POST "/next-challenge" req (next-challenge req))
  (POST "/send-message" req (post req))
  (POST "/fetch-config" req  (fetch-config req))
  (GET "/data" req (data-page req))
  (GET "/i/:image" [image] (serv-image image))
  (GET "/*" req (page)))

(def http-handler
  (if is-dev?
    (reload/wrap-reload (site #'routes))
    (site routes)))

(defn run [& [port]]
  (defonce ^:private server
    (do
      (if is-dev? (start-figwheel))
      (let [port (Integer. (or port (env :port) 10555))]
        (print "Starting web server on port" port ".\n")
        (run-jetty http-handler {:port  port
                                 :join? false}))))
  server)

(defn -main [& [port]]
  (run port))