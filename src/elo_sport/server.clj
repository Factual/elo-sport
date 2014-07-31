(ns elo-sport.server
  (:use [ring.middleware params keyword-params resource session]
        [compojure.core])
  (:require [compojure.route :as route]
            [elo-sport.core  :as elo]))

(defn exception-str
  [e]
  (with-out-str (.printStackTrace e (java.io.PrintWriter. *out*))))

(defn chat-session-id
  [req]
  (:chat-session-id (:session req)))

#_(defn say-something
  [{:keys [params] :as req}]
  (try
    {:status 200
     :body   (str (elo/query (:input params) (chat-session-id req)))}
    (catch Exception e
      {:status 500
       :body   (exception-str e)})))

(defn new-session
  [username]
  {:username username
   :session-id (.toString (java.util.UUID/randomUUID))})

(defn login-handler
  [{:keys [params] :as req}]
  (let [session (new-session (:username params))]
    {:status 200
     :body (str session)
     :session session}))

(defn hello-handler
  [{:keys [params] :as req}]
  (let [session (new-session (:username params))]
    {:status 200
     :body (str "Current session username: " (:username (:session req)))
     :session session}))

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
