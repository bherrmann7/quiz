(ns quiz.utils)


(defn l [& args]
  (.log js/console (clojure.string/join args)))


(defn ls [& args]
  (.log js/console (clojure.string/join " " args)))