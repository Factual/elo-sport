(ns elo-sport.core
  (:require [clojure.string :as str]))


(defn new-score [current-score game-score expected-score]
  (let [k 32]
    (+ current-score (* k (- game-score expected-score)))))

(defn qa [score]
  (Math/pow 10 (/ score 400)))

(defn expected-score [player-score opponent-score]
  (let [adj-player (qa player-score)
        adj-opponent (qa opponent-score)]
    (/ adj-player (+ adj-player adj-opponent))))

(defn game-score [score1 score2]
  (if (> score1 score2) 1 0))

(defn update-elo-scores [scores match]
  (let [challenger (keyword (:challenger match))
        opponent (keyword (:opponent match))
        ch-current (challenger scores)
        opp-current (opponent scores)
        ch-expected (expected-score ch-current opp-current)
        opp-expected (expected-score opp-current ch-current)
        ch-raw-score (:challenger-score match)
        opp-raw-score (:opponent-score match)
        ch-game-score (game-score ch-raw-score opp-raw-score)
        opp-game-score (game-score opp-raw-score ch-raw-score)
        challenger-new (new-score ch-current ch-game-score ch-expected)
        opponent-new (new-score opp-current opp-game-score opp-expected)]
    (merge scores {challenger challenger-new opponent opponent-new})))

(defn elo-scores [scores matches time]
  (let [valid-matches (filter #(< (:timestamp %) time) matches)
        sorted-matches (sort-by :timestamp valid-matches)]
    (reduce update-elo-scores scores sorted-matches)))
