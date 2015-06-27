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

(defn next-challenge [])

(defn post [req]
  (let [user (:user (:params req))
        pass (:password (:params req))
        ]
    (if (= "bob" user)
      (if (= "dk" pass)
        {:status 200 :body (pr-str {:user user}) :session {:user user}}
        ;(next-challenge req {:user user})
        {:status 200 :body (pr-str {:message "Bad Password"})}
        )
      {:status 200 :body (pr-str {:message "Bad Username"})}
      )))

(defn data-page [req]
  (map #(str "<div style='display:inline-block;margin: 5px;margin-top: 20px;text-align: center'><img width=300px src='/i/" (:image_name %) "'><br>" (:name %) " </div>") challenges)
  )

(defn serv-image [image]
  {:status 200
   :body   (new java.io.FileInputStream (str home-dir "/presidents/" (java.net.URLDecoder/decode image)))
   }
  )

(defn find-by-image [image]
  (first (filter #(= (:image_name %) image) challenges)))

(defn is-correct? [image answer]
  (if (= answer (:name (find-by-image image))) 1 0))

(defn extract-round [outcomes]
  (if (empty? outcomes)
    1
    (:round (first outcomes))))

(defn record-and-fetch-outcomes [user image answer]
  (let [
        current-outcomes (quiz.db/get-current-round-outcomes quiz.db/db-spec user)
        current-round (extract-round current-outcomes)
        ]
    (if (not (and (nil? image) (nil? answer)))
      (do
        (quiz.db/insert-outcome! quiz.db/db-spec user current-round image answer (is-correct? image answer) (new java.util.Date))
        (record-and-fetch-outcomes user nil nil)
        )

      (if (= (count current-outcomes) (count challenges))
        [(inc current-round) []]
        [current-round current-outcomes]))
    )
  )

(defn pick-next [outcomes]
  ["05_150.gif" "James Monroe"]
  )

(defn pick-fakes [outcomes name gender]
      (map :name (take 4 (shuffle (filter #(not (= name (:name %))) challenges)))))

(defn generate-next-challenge [user image answer]
  (let [
        [round outcomes] (record-and-fetch-outcomes user image answer)
        total-correct (count (filter #(= (:correct %) 1) outcomes))
        [image name gender] (pick-next outcomes)
        message {
                 :image       image
                 :name        name
                 :gender      gender
                 :round-size  (count challenges)
                 :total-count (count outcomes)
                 :fakes       (pick-fakes outcomes name gender)
                 }
        ]
    (println "sending -- " message)
    message
    )
  )

(defn next-challenge [req merge-session]
  (let [new-session (merge (:session req) merge-session)
        current-round ()]
    {
     :status  200
     ;   :body (pr-str (generate-next-challenge (:user new-session) (:image req ) (:answer req) ))
     :body    (pr-str (generate-next-challenge (:user new-session) nil nil )) ;"41_150.gif" "Bill Clinton"))
     :session new-session
     }
    )
  )

(defn fetch-config [req]
  {
   :status  200
   :body   (pr-str quiz.config/config)
   }
  )

(defroutes routes
           (resources "/")
           (resources "/react" {:root "react"})
           (GET "/next-challenge" req (next-challenge req nil))
           (POST "/next-challenge" req (next-challenge req nil))
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