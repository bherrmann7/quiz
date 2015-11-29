(ns quiz.navbar
  (:require [quiz.utils :as u]
            [quiz.state :as s]))

(defn do-logout []
  (set! (.-location js/window) (str js/context "/"))
  )

(defn do-decks []
  (swap! s/app-state dissoc :challenge )
  )

(defn do-about []

  )

(defn page-at []
  (let [page
  (if (contains? @s/app-state :challenge ) :quiz
    (if (contains? @s/app-state :user_id ) :decks
      :login))]
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
      ]

     (if (not= :login (page-at))
       [:ul.nav.navbar-nav.pull-right
        [:li [:a {:on-click #(do-logout)} "Logout"]]
        ;[:li {:class (if (= :about (page-at)) "active")}
        ; [:a {:on-click #(do-about) } "About"]]
        ]
       )
     ]]])
