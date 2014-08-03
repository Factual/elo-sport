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
            [elo-sport.db :as db]
            [elo-sport.views
             [ladder :refer [ladder-page]]
             [challenge :refer :all]]))


;; (defn exception-str [e]
;;   (with-out-str (.printStackTrace e (java.io.PrintWriter. *out*))))


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


(defn logout-handler [req]
  (if (get-in req [:session :username])
    (let [session {:username nil :session-id nil}]
      {:body (ladder-page (assoc req :session session))
       :session session})
    ;; not logged in
    (ladder-page req)))


(defn default-handler [req]
  (let [path-info (:path-info req)]
    (when (some (partial = path-info) ["" "/"])
      (ring.util.response/redirect (str (:context req) "ladder"))
;      {:body (ladder-page req)}
#_      {:status 200
       :body (str req)}
      )))


(defroutes elo-handlers
  (GET "/ladder" [] ladder-page)
  (GET "/login" [] login-page)
  (GET "/logout" [] logout-handler)
  (POST "/authenticate" [] authenticate-handler)
  (GET "/challenge-page" [] create-challenge-page)
  (GET "/update-page" [] update-challenge-page)
  (POST "/challenge" [] create-challenge)
  (POST "/update" [] update-challenge)
  (GET "/closed-challenges-page" [] closed-challenges-page)
  default-handler
  (route/not-found "Route not found."))


(def app
  (-> elo-handlers
      wrap-session
      wrap-keyword-params
      wrap-params))
