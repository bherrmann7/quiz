(ns quiz.app.tinker
  (:require [quiz.db.core]))

(println ":database-url env is" (:database-url environ.core/env))

(quiz.db.core/connect!)

;(println quiz.db.core/*conn*)


(println (quiz.db.core/get-decks {}  @quiz.db.core/*conn*))

;(defn run [query params] )
