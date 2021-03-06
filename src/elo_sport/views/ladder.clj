(ns elo-sport.views.ladder
  (:require [elo-sport.rating :as rating]
            [elo-sport.db :as db]
            [hiccup
             [form :refer :all]
             [core :refer :all]
             [page :refer :all]
             [element :refer :all]]
            [ring.util.response :refer [redirect]]
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


(defn redirect-to-ladder [req]
      (redirect (str (:context req) "/ladder")))


(defn ladder-page [{:keys [params] :as req}]
  (html5
   [:head
    [:title "Factual Ping Pong Ladder"]]
   [:body
    [:h1 "Factual Ping Pong Ladder"]

    (concat [:div]

     (let [username (get-in req [:session :username])]
       (if username

         ["Player: " username
          "&nbsp;&nbsp;" (link-to "logout" "Log out")
          "&nbsp;&nbsp;" (link-to "create-challenge-page" "Create challenge")
          "&nbsp;&nbsp;" (link-to "update-challenge-page" "Update challenge")]
         
         [(link-to "login" "Log in")]))

     ["&nbsp;&nbsp;" (link-to "closed-challenges-page" "Closed challenges")
      "&nbsp;&nbsp;" (link-to "admin-page" "Admin")])

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
