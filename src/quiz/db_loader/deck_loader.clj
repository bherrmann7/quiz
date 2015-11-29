(ns quiz.db-loader.deck-loader
  (:require [quiz.db.core]
            [ring.util.response :refer [response status]]
            [clojure.java.io :as io])
  (:use [taoensso.timbre :only [trace debug info warn error fatal]])
  (:import (java.sql BatchUpdateException DriverManager)
           (java.io FileInputStream)))

(defn create-deck [name path image-file card-count type]
  (quiz.db.core/insert-deck! {:name name  :image_data (new FileInputStream (str path image-file)) :card_count card-count
                              :type type}
                             @quiz.db.core/*conn*)
  (:id (some #(when (= name (:name %)) %) (quiz.db.core/get-decks @quiz.db.core/*conn*))))

(defn read-file [file-path]
  (with-open [reader (io/input-stream file-path)]
    (let [length (.length (io/file file-path))
          buffer (byte-array length)]
      (.read reader buffer 0 length)
      buffer)))

(defn load-card [deck-id type path name answer-data group]
  (let [conn  (DriverManager/getConnection (environ.core/env :database-url))
        ps (.prepareStatement conn (str "insert into cards (id, deck_id, name, grouping, enabled, image_data, answer)"
                                        " values           (null,     ?,    ?,       ?,        1,         ?,       ?)"))
        fis (if (= type "image") (new FileInputStream (str path answer-data)))
        answer (if (= type "text") answer-data)]
    (.setInt ps 1 deck-id)
    (.setString ps 2 name)
    (.setString ps 3 group)
    (.setBinaryStream ps 4 fis)
    (.setString ps 5 answer)
    (.executeUpdate ps)
    (.close ps)
    (if fis (.close fis))
    (.close conn)))

(defn load-cards-into-db [deck-id path card-pairs type grouping]
  (info "Grouping is " grouping)
  (doseq [[name answer-data] (partition 2 card-pairs)]
    (let [group (if-not (nil? grouping) ((eval grouping) [name answer-data] card-pairs))]
      ; This was failing to load files around 70k and higher using the yesql as the loader
     ; quiz.db.core/insert-card!
    (load-card deck-id type path name answer-data group))))

(defn load-deck [path]
  (let [{:keys [name image-file cards type grouping]}
        ; the try catch mostly helps me see the issue is in the deck.clj file, and not
        ; my own surrounding source code.
        (try (read-string (slurp (str path "deck.clj")))
             (catch Throwable t (error "Problem reading " path " " (.getMessage t))))
        deck-type (if (nil? type) "image" type)]
    (if (and name image-file cards grouping)
      (let [deck-id (create-deck name path image-file (/ (count cards) 2) deck-type)]
        (load-cards-into-db deck-id path cards deck-type grouping)))))

; run this, which loads the deck images into the database, by typing "lein run load"
(defn load-all-decks []
  (quiz.db.core/connect!)
  (quiz.db.core/delete-outcomes! @quiz.db.core/*conn*)
  (quiz.db.core/delete-rounds! @quiz.db.core/*conn*)
  (quiz.db.core/delete-decks! @quiz.db.core/*conn*)
  (quiz.db.core/delete-cards! @quiz.db.core/*conn*)
  (load-deck "resources/decks/small/")
  (load-deck "resources/decks/shapes/")
  (load-deck "resources/decks/presidents/")
  (load-deck "resources/decks/nerf/")
  (load-deck "resources/decks/clojure/")
  (load-deck "resources/decks/clojure2/")
  (load-deck "resources/decks/meetup/")
  (info "loading complete.")
  (System/exit 0))
