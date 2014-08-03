(ns elo-sport.server
  (:require [compojure
             [core  :refer :all]
             [route :as route]]
            [hiccup
             [form :refer :all]
             [core :refer :all]
             [page :refer :all]
             [element :refer :all]]
            [ring.middleware
             [session :refer :all]
             [params :refer :all]
             [keyword-params :refer :all]]
            [ring.util.response :refer [redirect]]
            [elo-sport.db :as db]
            [elo-sport.views
             [ladder :refer [ladder-page]]
             [challenge :refer :all]]))


(defn login-page [req] 
  (html5
   (form-to
    [:post "authenticate"]
    "Player name: "
    [:input {:type "text"
             :name "username"}]
    (submit-button "Log in"))))


(defn redirect-to-ladder [req]
      (redirect (str (:context req) "/ladder")))


(defn authenticate-handler [{:keys [params] :as req}]
  (let [ladder (redirect-to-ladder req)
        username (:username params)
        session (:session req)]
    (if (and session (= username (:username session)))
      ;; already logged in
      ladder
      (let [session {:username username
                     :session-id (.toString (java.util.UUID/randomUUID))}]
        (assoc ladder :session session)))))


(defn logout-handler [req]
  (let [ladder (redirect-to-ladder req)]
    (if (get-in req [:session :username])
      (let [session {:username nil :session-id nil}]
        (assoc ladder :session session))
      ;; not logged in
      ladder)))


(defn ladder-synonym-handler [req]
  ;; Redirects synonym URIs to /ladder, so relative links will always
  ;; have the same base.
  (let [path-info (:path-info req)]
    (when (some (partial = path-info) ["" "/" "/ladder/"])
      (redirect-to-ladder req))))


(defroutes elo-handlers
  (GET "/ladder" [] ladder-page)
  ladder-synonym-handler
  (GET "/login" [] login-page)
  (GET "/logout" [] logout-handler)
  (POST "/authenticate" [] authenticate-handler)
  (GET "/challenge-page" [] create-challenge-page)
  (GET "/update-page" [] update-challenge-page)
  (POST "/challenge" [] create-challenge)
  (POST "/update" [] update-challenge)
  (GET "/closed-challenges-page" [] closed-challenges-page)
  (route/not-found "Route not found."))


(def app
  (-> elo-handlers
      wrap-session
      wrap-keyword-params
      wrap-params))
