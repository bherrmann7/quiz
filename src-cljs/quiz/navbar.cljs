(ns quiz.navbar
  (:require [quiz.utils :as u]
            [quiz.state :as s]))

(defn do-logout []
  (set! (.-location js/window) "/")
  )

(defn do-decks []
  (swap! s/app-state dissoc :challenge )
  )

(defn do-about []
  (println "You should do-about!")
  )

(defn do-quiz []
  (println "You should do-about!")
  )

(defn page-at []
  (let [page
  (if (contains? @s/app-state :challenge ) :quiz
    (if (contains? @s/app-state :user_id ) :decks
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

     (if (not= :login (page-at))
       [:ul.nav.navbar-nav.pull-right
        [:li [:a {:on-click #(do-logout)} "Logout"]]]
       )

     ]]])
