(ns elo-sport.db
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern])
  (:use monger.operators))

;; {:host ... :db ...}
(def dbconfig (read-string (slurp (clojure.java.io/resource "dbconfig.clj"))))
(def conn (mg/connect {:host (:host dbconfig)}))
(def db (mg/get-db conn (:db dbconfig)))
(def extra-days-per-challenge 1)

(defn insert-player
  [username email]
  (mc/insert db "players" {:username username :email email}))

(defn get-players
  [query]
  (mc/find-maps db "players" query))

(defn get-matches
  [query]
  (mc/find-maps db "matches" query))

(defn find-open-matches-for-player
  [player]
  (let [username (:username player)]
    (get-matches {$or [{:challenger username :status :open}
                       {:opponent username :status :open}]})))

(defn calc-forfeit-date
  [created-at opponent]
  (let [opponent-open-matches (find-open-matches opponent)]
    nil))

(defn insert-match
  [challenger opponent]
  (let [created-at (System/currentTimeMillis)
        forfeit-date (calc-forfeit-date created-at opponent)]
    (mc/insert db "matches" {:challenger challenger :opponent opponent :created_at created-at :status :open})))


(defn update-match
  [challenger opponent challenger_score opponent_score note]
  (mc/update db "matches" {:challenger challenger :opponent opponent :status :open}
             {$set {:challenger_score challenger_score :opponent_score opponent_score :played_at (System/currentTimeMillis) :status :closed}}))
