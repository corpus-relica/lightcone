(ns rlc.lightcone.env
  (:require [clojure.tools.logging :as log]))

(do
  (log/info "ARCHIVIST_SERVICE_URL from env:" (System/getenv "ARCHIVIST_SERVICE_URL"))
  (log/info "CLARITY_SERVICE_URL from env:" (System/getenv "CLARITY_SERVICE_URL")))

(def ARCHIVIST_SERVICE_URL
  (let [url (or (System/getenv "ARCHIVIST_SERVICE_URL")
                "http://localhost:3000")]
    (log/info "Using ARCHIVIST_SERVICE_URL:" url)
    url))

(def CLARITY_SERVICE_URL
  (let [url (or (System/getenv "CLARITY_SERVICE_URL")
                "http://localhost:3001")]
    (log/info "Using CLARITY_SERVICE_URL:" url)
    url))
