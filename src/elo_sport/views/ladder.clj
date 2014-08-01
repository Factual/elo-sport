(ns elo-sport.views.ladder
  (:use [hiccup form core page element])
  (:require [elo-sport.rating :as rating]))


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
    (let [ratings (sort #(< (second %1) (second %2))
                        (seq (rating/all-elo-ratings)))]
      [:table
       (map (fn [[player rating]]
              [:tr
               [:td player]
               [:td rating]])
            ratings)])]))
