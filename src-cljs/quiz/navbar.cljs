(ns quiz.navbar
  (:require [quiz.utils :as u]))

(defn do-logout []
  (println "You should logout!")
  )

(defn do-decks []
  (swap! quiz.state/app-state dissoc :challenge )
  )

(defn do-about []
  (println "You should do-about!")
  )

(defn do-quiz []
  (println "You should do-about!")
  )

(defn page-at []
  (let [page
  (if (contains? @quiz.state/app-state :challenge ) :quiz
    (if (contains? @quiz.state/app-state :user_id ) :decks
      :login))]
  (u/l "at page" page)
  page)
  )

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand {:on-click do-decks} "Quiz"]]
    [:div.navbar-collapse.collapse
     [:ul.nav.navbar-nav
      (if (not= (page-at) :login)
        [:li {:class (if (= :decks (page-at)) "active")}
         [:a {:on-click #(do-decks)} "Decks"]]
        )

      #_(if (not= (page-at) :login)
        [:li {:class (if (= :about (page-at)) "active")}
         [:a {:on-click #(do-about) } "About"]]
        )
      ]

     #_(if (not= :login (page-at))
       [:ul.nav.navbar-nav.pull-right
        [:li [:a {:on-click #(do-logout)} "Logout"]]]
       )

     ]]])
