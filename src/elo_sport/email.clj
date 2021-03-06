(ns elo-sport.email
  (:require [postal.core :as post]
            [clj-time
             [format :as f]
             [coerce :as c]]))

(defn format-timestamp [timestamp]
  (f/unparse (f/formatters :date) (c/from-long timestamp)))

(defn challenge-email-subject
  [challenger]
  (str "Factual Ping Pong Ladder: " (:username challenger) " has challenged you."))


(defn challenge-email-body
  [match chal-message]
  (str "Note: you must compete and update the challenge by "
       (format-timestamp (:forfeit_date match)) " or the challenger moves up by forfeit.\n"
       "------------------------\n" chal-message))

(defn send-email
  [email-params]
  (post/send-message {:host "smtp1.factual.com"} email-params))

(defn send-challenge-email
  [challenger opponent match chal-message]
  (send-email {:from (:email challenger)
               :to (:email opponent)
               :cc (:email challenger)
               :subject (challenge-email-subject challenger)
               :body (challenge-email-body match chal-message)}))
