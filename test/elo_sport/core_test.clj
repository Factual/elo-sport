(ns elo-sport.core-test
  (:require [clojure.test :refer :all]
            [elo-sport.core   :refer :all]))

(deftest smoke-test
  (is (query "Hello, how are you?")))
