(ns quiz.db-challenges-loader)
(require '[yesql.core :refer [defqueries]])

(def home-dir (System/getProperty "user.home"))

(def db-spec {; :classname "org.postgresql.Driver"
              :subprotocol "mysql"
              :subname     "//localhost/quiz"
              :user        "quiz"
              :password    (.trim (slurp (str home-dir "/bin/quiz.db.pass")))})

(defn load-challenges [path]
  (read-string (slurp path))
  )

(defqueries "challenges.sql")

(defn breakList [list]
  (if (empty? list)
    nil
    (let [remaining (rest (rest list))]
      (add-challenge! db-spec (second list) (first list) )
      (breakList remaining)
      )
    )
  )

(delete-challenges! db-spec)

(breakList (load-challenges (str home-dir "/presidents/data.clj")))

(println "records: " (challenges-query db-spec))
(println "records loaded " (count (challenges-query db-spec)))