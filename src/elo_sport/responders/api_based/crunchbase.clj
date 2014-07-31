(ns elo-sport.responders.api-based.crunchbase
  (:use elo-sport.responders.api-based.base))

;; TODO: in core, require this namespace

(def crunchbase-responder
  (make-dataset-responder crunchbase-semparser crunchbase-api-caller default-api-response-renderer))

(register-responder ::crunchbase-responder crunchbase-responder)
