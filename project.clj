(defproject elo-sport "1.0.0-SNAPSHOT"
  :description "Elo rankings for ping pong, pool and other sports."
  :jar-exclusions [#"\.git"] ; not necessary in a future release of leiningen
  :main elo-sport.core
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler elo-sport.server/app}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]])
