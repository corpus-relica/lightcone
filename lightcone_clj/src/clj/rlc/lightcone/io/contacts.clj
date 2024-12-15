(ns rlc.lightcone.io.contacts
  (:require [clj-http.client :as http]
            [ring.util.response :as response]
            ;; [clojure.tools.logging :as log]
            ))

(def ^:private service-url "http://localhost:3000")

(defn get-people []
  (try
    (let [response (http/get (str service-url "/fact/classified")
                             {:query-params {:uid "990010"
                                             :recursive true}
                              :throw-exceptions false
                              :as :json})]
      (case (:status response)
        200 (let [foo (map (fn [person]
                             ;; physical object
                             {:uid (:lh_object_uid person)
                              :name (:lh_object_name person)
                              :nature :individual
                              :kind-ref (:rh_object_uid person)})
                           (:body response))]
              (response/response foo))
        404 (response/not-found {:error "No people found"
                                 :details (:body response)})
        (do
          ;; (log/error "Failed to fetch people:" response)
          (response/status 500 {:error "Failed to fetch people data"}))))
    (catch Exception e
      ;; (log/error e "Failed to connect to fact retrieval service")
      (response/status 500 {:error "Service connection error"}))))

(defn get-person-name [uid]
  (try
    (let [response (http/get (str service-url "/fact/classificationFact")
                             {:query-params {:uid uid}
                              :throw-exceptions false
                              :as :json})]
      (case (:status response)
        200 (let [person (first (:body response))]
              (response/response (:lh_object_name person)))
        404 (response/not-found {:error "No person found"
                                 :details (:body response)})
        (do
          ;; (log/error "Failed to fetch person:" response)
          (response/status 500 {:error "Failed to fetch person data"}))))
    (catch Exception e
      ;; (log/error e "Failed to connect to fact retrieval service")
      (response/status 500 {:error "Service connection error"}))))
