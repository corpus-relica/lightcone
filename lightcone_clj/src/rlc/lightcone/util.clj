(ns rlc.lightcone.util
  (:require [ring.util.response :as response]
            [rlc.lightcone.io.auth :as auth]))

(defn extract-token [request]
  (let [headers (:headers request)
        header-value (get headers "authorization")]
    (if header-value
      (second (clojure.string/split header-value #" "))
      nil)))
