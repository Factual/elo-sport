(ns elo-sport.challenge
  (:require [elo-sport.db :as db]
            [elo-sport.email :as email])
  (:use monger.operators))

(def FORFEIT-WIN-SCORE 3)
(def FORFEIT-LOSS-SCORE 0)

(defn create-challenge
  [challenger opponent message]
  (let [matches (db/get-matches {:status :open $or [{:challenger challenger :opponent opponent} {:challenger opponent :opponent challenger}]})
        insert-match-return (if (empty? matches) (db/insert-match challenger opponent))
        ch-map (db/get-players {:username challenger})
        op-map (db/get-players {:username opponent})
        match (db/get-matches {:status :open :challenger challenger :opponent opponent})]
    (if (insert-match-return) (email/send-challenge-email ch-map op-map (first match) message))))

(defn update-challenge
  [challenger opponent challenger-score opponent-score note]
  (db/update-match challenger opponent challenger-score opponent-score note))

(defn forfeit-match [match]
  (let [challenger (:challenger match)
        opponent (:opponent match)]
    (db/update-match challenger opponent FORFEIT-WIN-SCORE FORFEIT-LOSS-SCORE "forfeit")))

(defn update-all-forfeits
  []
  (let [matches (db/get-matches {:status :open :forfeit_date {$exists true}})
        now (System/currentTimeMillis)
        forfeits (filter #(< (:forfeit_date %) now) matches)]
    (doall (map #(forfeit-match %) forfeits))
    forfeits))
