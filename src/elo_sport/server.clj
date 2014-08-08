(ns elo-sport.server
  (:require [elo-sport.db :as db]
            [elo-sport.views
             [ladder :refer [redirect-to-ladder ladder-page]]
             [challenge :refer :all]]
            [compojure
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
             [keyword-params :refer :all]]))


(defn login-page [req] 
  (html5
   (form-to
    [:post "login-post"]
    "Player name: "
    [:input {:type "text"
             :name "username"}]
    (submit-button "Log in"))))


(defn login-handler [{:keys [params] :as req}]
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

  (GET "/login"                  [] login-page)
  (GET "/logout"                 [] logout-handler)
  (GET "/create-challenge"       [] create-challenge-page)
  (GET "/update-challenge"       [] update-challenge-page)
  (GET "/closed-challenges"      [] closed-challenges-page)
  (GET "/admin"                  [] admin-page)

  (POST "/login-post"            [] login-handler)
  (POST "/create-challenge-post" [] create-challenge-post)
  (POST "/update-challenge-post" [] update-challenge-post)
  (POST "/admin-post"            [] admin-post)

  (route/not-found "Route not found."))


(def app
  (-> elo-handlers
      wrap-session
      wrap-keyword-params
      wrap-params))
