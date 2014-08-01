(ns elo-sport.views.ladder
  (:require [elo-sport.rating :as rating]
            [elo-sport.db :as db]
            [hiccup
             [form :refer :all]
             [core :refer :all]
             [page :refer :all]
             [element :refer :all]]))


(defn sorted-ratings []
  (->> (rating/all-elo-ratings)
       seq
       (sort-by second >)
       (map (fn [[player rating]]
              [player (/ (Math/round (* rating 10.0)) 10.0)]))))


(defn match-table
  [matches time-key]
  (let [sorted-matches (sort-by time-key matches)]
    [:table
           (map (fn [[match]]
                  [:tr 
                   [:td (:challenger match)]
                   [:td (:opponent match)]
                   [:td (:challenger-score match)]
                   [:td (:opponent-score match)]
                   [:td (:note match)]
                   [:td (time-key match)]])
                sorted-matches)]))


(defn ladder-page [{:keys [params] :as req}]
  (html5
   [:head
    [:title "Factual Ping Pong Ladder"]]
   [:body
    [:h1 "Factual Ping Pong Ladder"]

    (let [username (get-in req [:session :username])]
      (if username

        [:div "Player: " username
         "&nbsp;&nbsp;"
         (link-to "logout"         "Log out")
         "&nbsp;&nbsp;"
         (link-to "challenge-page" "Create challenge")
         "&nbsp;&nbsp;"
         (link-to "update-page"    "Update challenge")
         "&nbsp;&nbsp;"
         (link-to "closed-challenges-page"    "Closed challenges")]
        
        [:div (link-to "login" "Log in")]))

    "<br>"

    (let [ratings (sorted-ratings)]
      [:table
       (map (fn [[player rating]]
              [:tr
               [:td player]
               [:td rating]])
            ratings)])

    "<br>"

  (let [matches (db/get-matches {:status :open})]
    (match-table matches :created_at))]))
