(ns elo-sport.views.admin
  (:require [elo-sport.rating :as rating]
            [elo-sport.db :as db]
            [elo-sport.challenge :as challenge]
            [elo-sport.views 
             [ladder :refer [redirect-to-ladder format-timestamp]]]
            [hiccup
             [form :refer :all]
             [core :refer :all]
             [page :refer :all]
             [element :refer :all]]
            [clj-time.core :as time]))


(defn admin-page
  [{:keys [params] :as req}]
  (html5
   (form-to [:post "admin-post"]
            (drop-down "dbcoll" (db/get-collections))
            (label "querylabel" "Query")
            (text-area {:rows "10"
                        :cols "30"})
            (label "updatelabel" "Update")
            (text-area {:rows "10"
                        :cols "30"})
            (submit-button "Submit"))
   (let [results (:query-results req)]
     results)))


(defn admin-post [req]
  (admin-page (assoc req :query-results nil)))
