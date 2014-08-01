(ns elo-sport.views.ladder
  (:use [hiccup core page]))

(defn ladder-page []
  (html5
    [:head
      [:title "Hello World"]
      (include-css "/css/style.css")]
    [:body
      [:h1 "Hello World"]]))
