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

(defn insert-player
  [username email]
  (mc/insert db "players" {:username username :email email}))

(defn calc-forfeit-date
  [created-at]
  nil)

(defn insert-match
  [challenger opponent]
  (let [created-at (System/currentTimeMillis)
        forfeit-date (calc-forfeit-date created-at)]
    (mc/insert db "matches" {:challenger challenger :opponent opponent :created_at  :status :open})))


(defn update-match
  [challenger opponent challenger_score opponent_score note]
  (mc/update db "matches" {:challenger challenger :opponent opponent :status :open}
             {$set {:challenger_score challenger_score :opponent_score opponent_score :played_at (System/currentTimeMillis) :status :closed}}))

(defn get-players
  [query]
  (mc/find-maps db "players" query))

(defn get-matches
  [query]
  (mc/find-maps db "matches" query))

