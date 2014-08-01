(ns elo-sport.views.ladder
  (:use [hiccup form core page]))

(defn ladder-page [{:keys [params] :as req}]
  (html5
   [:head
    [:title "Factual Ping Pong Ladder"]]
   [:body
    [:h1 "Factual Ping Pong Ladder"]
    (let [username (get-in req [:session :username])]
      (if username
        [:div "Player: " username]
        (form-to [:post "login"]
                 "Player name: "
                 [:input {:type "text"
                          :name "username"}]
                 (submit-button "Log in"))))]))
