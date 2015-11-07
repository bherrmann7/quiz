(ns quiz.db_loader.deck-loader
  (:require [quiz.db.core]
            [ring.util.response :refer [response status]]
            [clojure.java.io :as io])
  (:import (java.sql BatchUpdateException DriverManager)
           (java.io FileInputStream)))
()
(defn create-deck [name path image-file card-count]
  (quiz.db.core/insert-deck! {:name name  :image_data (new FileInputStream (str path image-file)) :card_count card-count}
                                                 @quiz.db.core/*conn*)
  (:id (some #(when (= name (:name %)) %) (quiz.db.core/get-decks @quiz.db.core/*conn*))))

(defn read-file [file-path]
  (with-open [reader (io/input-stream file-path)]
    (let [length (.length (io/file file-path))
          buffer (byte-array length)]
      (.read reader buffer 0 length)
      buffer)))

(defn load-card [path props]
  (let [
        conn  (DriverManager/getConnection (environ.core/env :database-url))
        ps (.prepareStatement conn "insert into cards values (null, ?, ?, ?, 1, ?)")
        fis (new FileInputStream (str path (:image-file props)))
        ]
    (.setInt ps 1 (:deck_id props))
    (.setString ps 2 (:name props))
    (.setString ps 3 nil)
    (.setBinaryStream ps 4 fis)
    (.executeUpdate ps)
    (.close ps)
    (.close fis)
    (.close conn)
    ))


(defn load-cards-into-db [deck-id path card-pairs]
  (doseq [[name image-file] (partition 2 card-pairs)]
    (let [image-data (read-file (str path image-file))
          ]
      ; This was failing to load files around 70k and higher
     ; quiz.db.core/insert-card!
    (load-card (str path image-file)
                                {:deck_id    deck-id,
                                :name       name
                                :grouping   nil
                                :image_data image-data}
                              ))))

(defn load-deck [path]
  (let [{:keys [name image-file cards]}
        ; the try catch mostly helps me see the issue is in the deck.clj file, and not
        ; my own surrounding source code.
        (try (read-string (slurp (str path "deck.clj")))
             (catch Throwable t (println "Problem reading " path " " (.getMessage t))))]
    (if (and name image-file cards)
      (let [deck-id (create-deck name path image-file (count cards))]
        (load-cards-into-db deck-id path cards)))))

; run this, which loads the deck images into the database, by typing "lein run load"
(defn load []
  (quiz.db.core/connect!)
  (quiz.db.core/delete-decks! @quiz.db.core/*conn*)
  (quiz.db.core/delete-cards! @quiz.db.core/*conn*)
  (load-deck "resources/decks/small/")
  (load-deck "resources/decks/shapes/")
  (load-deck "resources/decks/presidents/")
  (load-deck "resources/decks/nerf/"))
