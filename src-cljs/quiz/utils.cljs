(ns quiz.utils)


(defn l [& args]
  (.log js/console (clojure.string/join args)))


(defn ls [& args]
  (.log js/console (clojure.string/join " " args)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console
        (str "something bad happened: " status " " status-text)))
