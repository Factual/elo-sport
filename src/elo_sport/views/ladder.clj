(ns elo-sport.views.ladder
  (:require [elo-sport.rating :as rating]
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


(defn ladder-page [{:keys [params] :as req}]
  (html5
   [:head
    [:title "Factual Ping Pong Ladder"]]
   [:body
    [:h1 "Factual Ping Pong Ladder"]
    (let [username (get-in req [:session :username])]
      (if username
        [:div "Player: " username
         (link-to "logout" "Log out")]
        (link-to "login" "Log in")))
    (let [ratings (sorted-ratings)]
      [:table (:style "border: 1")
       (map (fn [[player rating]]
              [:tr
               [:td player]
               [:td rating]])
            ratings)])]))
