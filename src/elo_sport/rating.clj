(ns elo-sport.rating
  (:require [elo-sport.db :as db]))

(def K-FACTOR 32)

(defn get-all-players 
  []  
  (map #(:username %) (db/get-players {})))

(defn get-all-closed-matches 
  []
  (db/get-matches {:status :closed}))

(defn generate-start-ratings [players]
  (into {} (map #(hash-map (keyword %) 1500) players)))

(defn qa [rating]
  (Math/pow 10 (/ rating 400)))

(defn expected-rating [player-rating opponent-rating]
  (let [adj-player (qa player-rating)
        adj-opponent (qa opponent-rating)]
    (/ adj-player (+ adj-player adj-opponent))))

(defn match-score [score1 score2]
  (if (> score1 score2) 1 0))

(defn new-rating [current-rating match-score expected-rating]
  (+ current-rating (* K-FACTOR (- match-score expected-rating))))

(defn update-elo-ratings [ratings match]
  (let [challenger (keyword (:challenger match))
        opponent (keyword (:opponent match))
        ch-rating (challenger ratings)
        opp-rating (opponent ratings)
        ch-expected (expected-rating ch-rating opp-rating)
        opp-expected (expected-rating opp-rating ch-rating)
        ch-raw-score (:challenger_score match)
        opp-raw-score (:opponent_score match)
        ch-match-score (match-score ch-raw-score opp-raw-score)
        opp-match-score (match-score opp-raw-score ch-raw-score)
        ch-new (new-rating ch-rating ch-match-score ch-expected)
        opp-new (new-rating opp-rating opp-match-score opp-expected)]
    (merge ratings {challenger ch-new opponent opp-new})))

(defn calculate-elo-ratings [ratings matches]
  (let [sorted-matches (sort-by :played_at matches)]
    (reduce update-elo-ratings ratings sorted-matches)))

(defn all-elo-ratings
  []
  (let [ratings (generate-start-ratings (get-all-players))]
    (calculate-elo-ratings ratings (get-all-closed-matches))))
