(ns rlc.lightcone.io.contacts
  (:require [clj-http.client :as http]
            [ring.util.response :as response]
            ;; [clojure.tools.logging :as log]
            [rlc.lightcone.env :refer [ARCHIVIST_SERVICE_URL]]
            [rlc.lightcone.http.client :as client]))

;;  TODO this namespace probably belongs under app

(defn get-people [token]
  (client/auth-get
    (str ARCHIVIST_SERVICE_URL "/fact/classified")
    token
    {:query-params {:uid "990010"
                    :recursive true}
     :transform-fn (fn [response-body]
                    (map (fn [person]
                          {:uid (:lh_object_uid person)
                           :name (:lh_object_name person)
                           :nature :individual
                           :kind-ref (:rh_object_uid person)})
                        response-body))}))

(defn get-person-name [uid token]
  (client/auth-get
    (str ARCHIVIST_SERVICE_URL "/fact/classificationFact")
    token
    {:query-params {:uid uid}
     :transform-fn (fn [response-body]
                    (:lh_object_name (first response-body)))}))
