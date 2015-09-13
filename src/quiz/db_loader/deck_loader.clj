(ns quiz.db_loader.deck-loader
  (:require [quiz.db.core]
            [ring.util.response :refer [response status]]
            [clojure.java.io :as io])
  (:import (java.sql BatchUpdateException)
           (java.io FileInputStream)))

(defn create-deck [name path image-file card-count]
  (println "inserted" (quiz.db.core/insert-deck! {:name name  :image_data (new FileInputStream (str path image-file)) :card_count card-count}
                                                 @quiz.db.core/*conn*))
  (println "decks" (quiz.db.core/get-decks @quiz.db.core/*conn*))
  (:id (some #(when (= name (:name %)) %) (quiz.db.core/get-decks @quiz.db.core/*conn*))))

(defn load-cards-into-db [deck-id path card-pairs]
  (println "I should insert deck-id" deck-id "with " (count card-pairs) " cards ")
  (doseq [[name image-file] (partition 2 card-pairs)]
    (println "Loading" name path image-file)
    (quiz.db.core/insert-card! {:deck_id    deck-id,
                                :name       name
                                :grouping   nil
                                :image_data (new FileInputStream (str path image-file))} @quiz.db.core/*conn*)))

(defn load-deck [path]
  (let [{:keys [name image-file cards]}
        ; the try catch mostly helps me see the issue is in the deck.clj file, and not
        ; my own surrounding source code.
        (try (read-string (slurp (str path "deck.clj")))
             (catch Throwable t (println "Problem reading " path " " (.getMessage t))))]
    (if (and name image-file cards)
      (let [deck-id (create-deck name path image-file (count cards))]
        (load-cards-into-db deck-id path cards)))))

(defn load []
  (quiz.db.core/connect!)
  (quiz.db.core/delete-decks! @quiz.db.core/*conn*)
  (quiz.db.core/delete-cards! @quiz.db.core/*conn*)
  (load-deck "resources/decks/shapes/")
  (load-deck "resources/decks/presidents/"))
