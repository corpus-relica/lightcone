(ns rlc.lightcone.env)


(def ARCHIVIST_SERVICE_URL
  (or (System/getenv "ARCHIVIST_SERVICE_URL")
      "http://localhost:3000"))

(def CLARITY_SERVICE_URL
  (or (System/getenv "CLARITY_SERVICE_URL")
      "http://localhost:3001"))
