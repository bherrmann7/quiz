
; Yea, this name is pretty uninspired - it has all the main game logic

(ns quiz.main
  (:require [clojure.java.io :as io]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [quiz.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [quiz.db]
            [quiz.config]))

(deftemplate page
  (io/resource "index.html") [] [:body] (if is-dev? inject-devmode-html identity))

; We assume the challenges are updated while we are running.
(def challenges (quiz.db/challenges-query quiz.db/db-spec))

(def total-challenges  (count challenges))

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

; Humm... should we actually just serve these images from the DB?
(defn serv-image [image]
  {:status 200
   :body   (new java.io.FileInputStream (str (:deck-dir quiz.config/config) (java.net.URLDecoder/decode image)))})

(defn record-and-fetch-outcomes [user correct answer]
  ; fetch the current round's values
  (let [{:keys [outcomes_count round correct_count]} (first (quiz.db/count-current-outcomes quiz.db/db-spec user))]
    (if (empty? answer)
      [outcomes_count round correct_count]
      ; If the incoming answer is the first in a new round, bump the round to the right value
      (let [round (if (= outcomes_count total-challenges) (inc round) round)]
        (quiz.db/insert-outcome! quiz.db/db-spec user round correct answer (= correct answer))
        [(inc outcomes_count) round (if (= correct answer) (inc correct_count) correct_count)]))))

;
(defn pick-next [user  current-round]
  (quiz.db/pick-next quiz.db/db-spec user current-round))

(defn pick-fakes [name gender]
  (map :name (take 4 (shuffle (filter #(not= name (:name %)) challenges)))))

(defn generate-next-challenge [user correct answer]
  (let [[outcomes-count round correct_count] (record-and-fetch-outcomes user correct answer)
        [{:keys [image_name name gender]}] (pick-next user round)]
    (let [message {:image       image_name
                   :name        name
                   :gender      gender
                   :round-size   total-challenges
                   :total-count  (:outcomes_count (first (quiz.db/count-current-outcomes quiz.db/db-spec user)))
                   :correct_count  (.intValue correct_count)
                   :fakes       (pick-fakes name gender)}]
      message)))

(defn next-challenge [req]
  (let [;new-session (merge (:session req) )
        user-choice (get (:form-params req) "user-choice")
        correct  (get (:form-params req) "correct")]
    {:status  200
     ;   :body (pr-str (generate-next-challenge (:user new-session) (:image req ) (:answer req) ))
     :body    (pr-str (generate-next-challenge "guest" correct user-choice)) ;"41_150.gif" "Bill Clinton"))
     ;:session new-session
     }))

(defn start-next-round [req]
  (let [user "guest"
        [round] (record-and-fetch-outcomes user nil nil)
        [{:keys [image_name name gender]}] (pick-next user round)
        message {:image         image_name
                 :name          name
                 :gender        gender
                 :round-size    total-challenges
                 :total-count   0
                 :correct_count 0
                 :fakes         (pick-fakes name gender)}]
    {:status 200
     :body   (pr-str message)}))

(defn fetch-config [req]
  {:status  200
   :body   (pr-str quiz.config/config)})
