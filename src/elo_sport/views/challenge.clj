(ns elo-sport.views.challenge
  (:require [elo-sport.rating :as rating]
            [elo-sport.db :as db]
            [elo-sport.views 
             [ladder :refer [ladder-page]]]
            [hiccup
             [form :refer :all]
             [core :refer :all]
             [page :refer :all]
             [element :refer :all]]))


(defn create-challenge-page 
[{:keys [params] :as req}]
  (html5
   (form-to
    [:post "challenge"]
    "Opponent name: "
    [:input {:type "text"
             :name "opponent"}]
    (submit-button "Submit Challenge"))))

(defn create-challenge
  [{:keys [params] :as req}]
  (db/insert-match (get-in req [:session :username]) (:opponent params))
  (ladder-page req))

(defn update-challenge-page 
[{:keys [params] :as req}]
  (html5
   (form-to
    [:post "update"]
    [:input {:type "text"
             :name "opponent"}]
    [:input {:type "text"
             :name "opponent-score"}]
    [:input {:type "text"
             :name "challenger-score"}]
    [:input {:type "text"
             :name "note"}])))

(defn update-challenge
  [{:keys [params] :as req}]
  (db/update-match (get-in req [:session :username])
                   (:opponent params)
                   (:challenger-score params)
                   (:opponent-score params)
                   (:note params)))
