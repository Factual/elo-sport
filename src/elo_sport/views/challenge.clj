(ns elo-sport.views.challenge
  (:require [elo-sport.rating :as rating]
            [elo-sport.db :as db]
            [elo-sport.views 
             [ladder :refer [redirect-to-ladder format-timestamp]]]
            [hiccup
             [form :refer :all]
             [core :refer :all]
             [page :refer :all]
             [element :refer :all]]
            [clj-time.core :as time]))


(defn create-challenge-page 
[{:keys [params] :as req}]
  (html5
   (form-to
    [:post "challenge"]
    "Opponent name: "
    [:input {:type "text"
             :name "opponent"}]
    (submit-button "Create challenge"))))


(defn create-challenge
  [{:keys [params] :as req}]
  (db/insert-match (get-in req [:session :username]) (:opponent params))
  (redirect-to-ladder req))


(defn update-challenge-page 
  [{:keys [params] :as req}]
  (html5
   (form-to
    [:post "update"]
    [:table
     [:tr
      [:td "Opponent name: "]
      [:td [:input {:type "text"
                    :name "opponent"}]]]
     [:tr
      [:td "Your score: "]
      [:td [:input {:type "text"
                    :name "challenger-score"}]]]
     [:tr
      [:td "Opponent score: "]
      [:td [:input {:type "text"
                    :name "opponent-score"}]]]
     [:tr
      [:td "Note: "]
      [:td [:input {:type "text"
                    :name "note"}]]]]
    "<br>"
    (submit-button "Update challenge"))))


(defn update-challenge
  [{:keys [params] :as req}]
  (db/update-match (get-in req [:session :username])
                   (:opponent params)
                   (read-string (:challenger-score params))
                   (read-string (:opponent-score params))
                   (:note params))
  (redirect-to-ladder req))


(defn closed-challenges-page
  [req]
  (html5
   (let [matches (db/get-matches {:status :closed})
         sorted-matches (sort-by :played_at matches)]
     [:table
      [:tr
       [:th "Challenger"]
       [:th "Opponent"]
       [:th "Challenger Score"]
       [:th "Opponent Score"]
       [:th "Note"]
       [:th "Played date"]]
      (map (fn [match]
             [:tr 
              [:td (:challenger match)]
              [:td (:opponent match)]
              [:td (:challenger_score match)]
              [:td (:opponent_score match)]
              [:td (:note match)]
              [:td (format-timestamp (:played_at match))]])
           sorted-matches)])
   "<br>"
   (link-to "ladder" "Ladder home")))
