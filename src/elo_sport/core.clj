(ns elo-sport.core
  (:require [elo-sport.register :refer [responders]]
            [elo-sport.responder.nonsense]
            [elo-sport.responder.sleeper]
            [elo-sport.responder.bored]
            [elo-sport.responder.reflector]
            [elo-sport.responder.delegator]
            [elo-sport.history    :refer [add-to-history!]]
            [elo-sport.middleware :refer [tokenize is-question?]]
            [clojure.string :as str]))

(defn analyzers [m]
  (-> m
      tokenize
      is-question?))

(def timeout-period 200)

(defn query [input & [chat-session-id]]
  (let [input-map (analyzers {:input input :chat-session-id chat-session-id})
        sorted-responders (sort-by (comp - :confidence)
                                   (map #(% input-map)
                                        (vals @responders)))
        output-map (some #(deref (:response-ref %)
                                 timeout-period
                                 nil)
                       sorted-responders)]
    (when chat-session-id
      (add-to-history! chat-session-id [input-map output-map]))
    (:output output-map)))

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

(defn chat-loop []
  (loop []
    (print "Eliza> ")
    (flush)
    (when-let [input (not-empty (read-line))]
      (println (query (str/trim input)))
      (recur))))
