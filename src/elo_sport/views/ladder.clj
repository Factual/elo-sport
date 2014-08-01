(ns elo-sport.views.ladder
  (:use [hiccup form core page]))

(defn ladder-page []
  (html5
   [:head
    [:title "Factual Ping Pong Ladder"]]
   [:body
    [:h1 "Hello World"]
    (form-to [:post "/login"]
             "username:"
             [:input {:type "text"
                      :name "username"}]
             (submit-button "Submit"))]))
