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
            [ring.adapter.jetty :refer [run-jetty]])
  (:use  [quiz.main]))

(defroutes routes
  (resources "/")
  (resources "/react" {:root "react"})
  (GET "/next-challenge" req (next-challenge req))
  (POST "/next-challenge" req (next-challenge req))
  (GET "/start-next-round" req (start-next-round req))
  (POST "/send-message" req (post req))
  (POST "/fetch-config" req  (fetch-config req))
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