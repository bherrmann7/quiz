(ns quiz.db)

(require '[yesql.core :refer [defqueries]])

(def home-dir (System/getProperty "user.home"))

(def db-spec {; :classname "org.postgresql.Driver"
              :subprotocol "mysql"
              :subname     "//localhost/quiz"
              :user        "quiz"
              :password    (.trim (slurp (str home-dir "/bin/quiz.db.pass")))})


(defqueries "challenges.sql")


