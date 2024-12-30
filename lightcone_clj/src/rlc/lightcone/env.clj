(ns rlc.lightcone.env)

(print (str "CLARITY_SERVICE_URL: " (System/getenv "CLARITY_SERVICE_URL")))
(print (str "ARCHIVIST_SERVICE_URL: " (System/getenv "ARCHIVIST_SERVICE_URL")))

(def ARCHIVIST_SERVICE_URL
  (or (System/getenv "ARCHIVIST_SERVICE_URL")
      "http://localhost:3000"))

(def CLARITY_SERVICE_URL
  (or (System/getenv "CLARITY_SERVICE_URL")
      "http://localhost:3001"))
