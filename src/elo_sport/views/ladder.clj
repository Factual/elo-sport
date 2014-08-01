(ns elo-sport.views.ladder
  (:require [elo-sport.rating :as rating]
            [elo-sport.db :as db]
            [hiccup
             [form :refer :all]
             [core :refer :all]
             [page :refer :all]
             [element :refer :all]]
            [clj-time
             [format :as f]
             [coerce :as c]]))


(defn sorted-ratings []
  (->> (rating/all-elo-ratings)
       seq
       (sort-by second >)
       (map (fn [[player rating]]
              [player (/ (Math/round (* rating 10.0)) 10.0)]))))


(defn format-timestamp [timestamp]
  (f/unparse (f/formatters :date) (c/from-long timestamp)))


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
        
        [:div
         (link-to "login" "Log in")
         "&nbsp;&nbsp;"
         (link-to "closed-challenges-page"    "Closed challenges")]))


    "<br>"

    (let [ratings (sorted-ratings)]
      [:table
       (map (fn [[player rating]]
              [:tr
               [:td player]
               [:td rating]])
            ratings)])

    "<br>"

    (let [matches (db/get-matches {:status :open})
          sorted-matches (sort-by :created_at matches)]
      [:table
       [:tr
        [:th "Challenger"]
        [:th "Opponent"]
        [:th "Created date"]
        [:th "Forfeit date"]]
       (map (fn [match]
              [:tr 
               [:td (:challenger match)]
               [:td (:opponent match)]
               [:td (format-timestamp (:created_at match))]
               [:td (format-timestamp (:forfeit_date match))]])
            sorted-matches)])]))
