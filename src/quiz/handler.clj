(ns quiz.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [quiz.layout :refer [error-page]]
            [quiz.routes.home :refer [home-routes]]
            [quiz.routes.services :refer [service-routes]]
            [quiz.middleware :as middleware]
            [quiz.db.core :as db]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []

  (timbre/merge-config!
   {:level     (if (env :dev) :trace :info)
    :appenders {:rotor (rotor/rotor-appender
                        {:path "quiz.log"
                         :max-size (* 512 1024)
                         :backlog 10})}})
  (timbre/info (str "Trying to connect to" (env :quiz-database-url)))
  (timbre/info (str "env has " (System/getenv "QUIZ_DATABASE_URL")))
  (if (env :dev) (parser/cache-off!))
  (db/connect!)
  (timbre/info (str
                "\n-=[quiz started successfully"
                (when (env :dev) " using the development profile")
                "]=-")))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "quiz is shutting down...")
  (db/disconnect!)
  (timbre/info "shutdown complete!"))

(def app-routes
  (routes
   (var service-routes)
    ;(wrap-routes #'home-routes middleware/wrap-csrf)
   (var home-routes)
   (route/not-found
    (:body
     (error-page {:status 404
                  :title "page not found"})))))

(def app (middleware/wrap-base #'app-routes))
