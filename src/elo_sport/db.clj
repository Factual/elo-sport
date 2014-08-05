(ns elo-sport.db
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [clj-time.core :as time]
            [clj-time.coerce :as translate-time])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern])
  (:use monger.operators))

;; {:host ... :db ...}
(def dbconfig (read-string (slurp (clojure.java.io/resource "dbconfig.clj"))))
(def conn (mg/connect {:host (:host dbconfig)}))
(def db (mg/get-db conn (:db dbconfig)))
(def extra-days-per-challenge 1)
(def extra-days-standard 2)

(defn insert-player
  [username email]
  (mc/insert db "players" {:username username :email email}))

(defn get-players
  "Query Examples: Find all players with username = 'Bob'
      query=> {:username \"Bob\"}
  Find all players with username = 'Bob' and email = bob@foo.com
      query => {:username \"Bob\" :email \"bob@foo.com\"}"
  [query]
  (mc/find-maps db "players" query))

(defn update-player
  "Update players with username = Bob, to have email = 'bob@foo.com'
     username \"Bob\" update => {:email \"bob@foo.com\"}"
  [username update]
  (mc/update db "players" {:username username} {$set update}))

(defn get-matches
  [query]
  (mc/find-maps db "matches" query))

(defn find-open-matches-for-player
  [player-username]
  (get-matches {$or [{:challenger player-username :status :open}
                     {:opponent player-username :status :open}]}))

(defn decrement-extra-week-days
  [dow extra-week-days]
  (if (or (= 5 dow)
          (= 6 dow))
    extra-week-days
    (- extra-week-days 1)))

(defn add-day
  [dow]
  (if (= dow 7)
    1
    (+ 1 dow)))

(defn ewdted-helper
  [dow extra-week-days days]
  (let [next-extra-week-days (decrement-extra-week-days dow extra-week-days)]
    (if (= 0 extra-week-days)
      days
      (ewdted-helper (add-day dow) next-extra-week-days (+ 1 days)))))

(defn extra-week-days-to-extra-days
  [dow extra-week-days]
  (if (= 0 extra-week-days)
    0
    (ewdted-helper dow extra-week-days 0)))

(defn calc-forfeit-date
  [created-at opponent-username]
  (let [num-opponent-open-matches (count (find-open-matches-for-player opponent-username))
        created-date (translate-time/from-long created-at)
        day-of-week (time/day-of-week created-date) ;;integer 1=monday 7=sunday
        extra-week-days (+ extra-days-standard (* num-opponent-open-matches extra-days-per-challenge))
        extra-days (extra-week-days-to-extra-days day-of-week extra-week-days)
        forfeit-date (time/plus created-date (time/days (+ 1 extra-days)))
        forfeit-date (time/date-midnight (time/year forfeit-date) (time/month forfeit-date) (time/day forfeit-date))
        forfeit-timestamp (translate-time/to-long forfeit-date)]
    forfeit-timestamp))

(defn insert-match
  [challenger-username opponent-username]
  (let [created-at (System/currentTimeMillis)
        forfeit-date (calc-forfeit-date created-at opponent-username)]
    (mc/insert db "matches" {:challenger challenger-username :opponent opponent-username :created_at created-at :status :open :forfeit_date forfeit-date})))


(defn update-match
  [challenger-username opponent-username challenger_score opponent_score note]
  (mc/update db "matches" {:challenger challenger-username :opponent opponent-username :status :open}
             {$set {:challenger_score challenger_score :opponent_score opponent_score :played_at (System/currentTimeMillis) :status :closed :note note}}))


(defn admin-edit-challenges
  "Example: Update open match with challenger username = Bob to be closed with opponent_score = 3, 
  challenger_score = 0 and note = 'just an admin thing'
      query => {:username \"Bob\" :status :open}
      update => {:challenger_score 0 :opponent_score 3 :status :closed :not \"just an admin thing\"}"
  [query update]
  (mc/update db "matches" query {$set update}))
