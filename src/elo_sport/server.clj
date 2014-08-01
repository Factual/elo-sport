(ns elo-sport.server
  (:use  [ring.middleware params keyword-params resource session]
         [elo-sport.views ladder])
  (:require [compojure.core  :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]))


(defn exception-str
  [e]
  (with-out-str (.printStackTrace e (java.io.PrintWriter. *out*))))


#_(defn foo
  [{:keys [params] :as req}]
  (try
    {:status 200
     :body   (str nil)}
    (catch Exception e
      {:status 500
       :body   (exception-str e)})))

(defn new-session
  [username]
  {:username username
   :session-id (.toString (java.util.UUID/randomUUID))})


(defn login-handler
  [{:keys [params] :as req}]
  (let [username (:username params)
        session (:session req)]
    (if (and session (= username (:username session)))
      {:status 200
       :body "Already logged in."}
      {:status 200
       :body "Logged in."
       :session (new-session username)})))


(defn hello-handler
  [{:keys [params] :as req}]
  (let [session (:session req)]
    {:status 200
     :body (str "Req: " req " Current session username: " (get-in req [:session :username]))}))


(defroutes elo-handlers
  (GET "/" [] ladder-page)
  (POST "/login" [] login-handler)
  (GET "/hello" [] hello-handler)
  (route/not-found "Page not found."))


(def app
  (-> elo-handlers
      (wrap-resource "public")
      wrap-session
      wrap-keyword-params
      wrap-params))
