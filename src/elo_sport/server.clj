(ns elo-sport.server
  (:use  [ring.middleware params keyword-params resource session])
  (:require [compojure.core  :refer :all]
            [compojure.route :as route]
            [elo-sport.score :as score]
            [elo-sport.user  :as user]))

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
     :body (str "Current session username: " (get-in req [:session :username]))}))

(defroutes elo-handlers
  (POST "/login" [] login-handler)
  (GET "/hello" [] hello-handler)
  (route/not-found "Route not found."))

(defn wrap-dir-index [handler]
  (fn [req]
    (handler
     (update-in req [:uri]
                #(if (= "/" %) "/ladder.html" %)))))

(def app
  (-> elo-handlers
      (wrap-resource "public")
      wrap-session
      wrap-dir-index
      wrap-keyword-params
      wrap-params))
