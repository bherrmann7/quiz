(ns quiz.db.migrations
  (:use [taoensso.timbre :only [trace debug info warn error fatal]])
  (:require
   [migratus.core :as migratus]
   [environ.core :refer [env]]
   [to-jdbc-uri.core :refer [to-jdbc-uri]]))

(defn parse-ids [args]
  (map #(Long/parseLong %) (rest args)))

(defn migrate [args]
  (warn "migrate clalled...")
  (let [config {:store :database
                :db {:connection-uri (to-jdbc-uri (:quiz-database-url env))}}]
    (warn "database quiz-database-url" (:quiz-database-url env))
    (case (first args)
      "migrate"
      (if (> (count args) 1)
        (apply migratus/up config (parse-ids args))
        (migratus/migrate config))
      "rollback"
      (if (> (count args) 1)
        (apply migratus/down config (parse-ids args))
        (migratus/rollback config)))))
