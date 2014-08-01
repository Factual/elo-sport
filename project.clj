(defproject elo-sport "1.0.0-SNAPSHOT"
  :description "Elo rankings for ping pong, pool and other sports."
  :jar-exclusions [#"\.git"] ; not necessary in a future release of leiningen
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler elo-sport.server/app}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [hiccup "1.0.5"]
                 [com.novemberain/monger "2.0.0"]
                 [com.draines/postal "1.11.1"]])
