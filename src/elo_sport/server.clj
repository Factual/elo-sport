(ns elo-sport.server
  (:require [compojure.core  :refer :all]
            [compojure.route :as route]
            [elo-sport.db :as db]
            [elo-sport.views
             [ladder :refer [ladder-page]]
             [challenge :refer [create-challenge-page update-challenge-page]]]
            [hiccup
             [form :refer :all]
             [core :refer :all]
             [page :refer :all]
             [element :refer :all]]
            [ring.middleware
             [session :refer [wrap-session]]
             [params :refer [wrap-params]]
             [keyword-params :refer [wrap-keyword-params]]]))


(defn exception-str [e]
  (with-out-str (.printStackTrace e (java.io.PrintWriter. *out*))))


(defn login-page [req] 
  (html5
   (form-to
    [:post "authenticate"]
    "Player name: "
    [:input {:type "text"
             :name "username"}]
    (submit-button "Log in"))))


(defn authenticate-handler [{:keys [params] :as req}]
  (let [username (:username params)
        session (:session req)]
    (if (and session (= username (:username session)))
      ;; already logged in
      (ladder-page req)
      (let [session {:username username
                     :session-id (.toString (java.util.UUID/randomUUID))}]
        {:body (ladder-page (assoc req :session session))
         :session session}))))


(defn logout-handler
  [req]
  (if (get-in req [:session :username])
    (let [session {:username nil :session-id nil}]
      {:body (ladder-page (assoc req :session session))
       :session session})
    ;; not logged in
    (ladder-page req)))


(defroutes elo-handlers
  (GET "/" [] ladder-page)
  (GET "/login" [] login-page)
  (GET "/logout" [] logout-handler)
  (POST "/authenticate" [] authenticate-handler)
  (GET "/challenge" [] create-challenge-page)
  (GET "/update" [] update-challenge-page)
  (route/not-found "Page not found."))


(def app
  (-> elo-handlers
      wrap-session
      wrap-keyword-params
      wrap-params))
