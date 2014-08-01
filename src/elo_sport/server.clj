(ns elo-sport.server
  (:use  [ring.middleware params keyword-params resource session]
         [elo-sport.views ladder])
  (:require [compojure.core  :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]))


(defn exception-str
  [e]
  (with-out-str (.printStackTrace e (java.io.PrintWriter. *out*))))


(defn login-handler
  [{:keys [params] :as req}]
  (let [username (:username params)
        session (:session req)]
    (if (and session (= username (:username session)))
      "Already logged in."
      {:body "Logged in."
       :session {:username username
                 :session-id (.toString (java.util.UUID/randomUUID))}})))


(defn logout-handler
  [{:keys [params] :as req}]
  (if (get-in req [:session :username])
    {:body "Logged out."
     :session {:username nil :session-id nil}}
    "Not logged in."))


(defroutes elo-handlers
  (GET "/" [] ladder-page)
  (POST "/login" [] login-handler)
  (POST "/logout" [] logout-handler)
  (route/not-found "Page not found."))


(def app
  (-> elo-handlers
      (wrap-resource "public")
      wrap-session
      wrap-keyword-params
      wrap-params))
