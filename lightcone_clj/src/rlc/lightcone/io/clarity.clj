(ns rlc.lightcone.io.clarity
  (:require [clj-http.client :as http]
            [ring.util.response :as response]
            ;; [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [rlc.lightcone.env :refer [CLARITY_SERVICE_URL]]))

(defn get-all-events [token]
  (tap> "GETTING ALL EVENTS from CLARITY")
  (tap> token)
  (try
    (let [response (http/get (str CLARITY_SERVICE_URL "/events/list")
                             {:throw-exceptions false
                              :as :json
                              :headers {"Authorization" (str "Bearer " token)}})]
      (tap> "GOT EVENTS ------->")
      (tap> response)
      (case (:status response)
        200 (response/response (:body response))
        401 (response/status 401 {:error "Unauthorized"})
        404 (response/not-found {:error "No events found"
                                 :details (:body response)})
        (do
          ;; (log/error "Failed to fetch events:" response)
          (response/status 500 {:error "Failed to fetch events data"}))))))

(defn get-event [uid token]
  (tap> "GETTING AN EVENT from CLARITY")
  (try
    (let [response (http/get (str CLARITY_SERVICE_URL "/event")
                             {:query-params {:uid uid}
                              :throw-exceptions false
                              :as :json
                              :headers {"Authorization" (str "Bearer " token)}})]
      (case (:status response)
        200 (let [body (first (:body response))
                  event {:uid (:lh_object_uid body)
                         :title (:lh_object_name body)
                         :event-type :event
                         :time nil
                         :participants nil}]
              (tap> "GOT EVENT ------->")
              (tap> event)
              (response/response event))
        404 (response/not-found {:error "No event found"
                                 :details (:body response)})
        (do
          ;; (log/error "Failed to fetch event:" response)
          (tap> "FAILED TO FETCH EVENT"
                (tap> response))
          (response/status 500 {:error "Failed to fetch event data"}))))
    (catch Exception e
      ;; (log/error e "Failed to connect to fact retrieval service")
      (tap> "FAILED TO FETCH EVENT EXCEPTION")
      (tap> e)
      (response/status 500 {:error "Service connection error"}))))


(defn get-event-time-value [uid token]
  (tap> "GETTING EVENT TIME VALUE from CLARITY")
  (try
    (let [response (http/get (str CLARITY_SERVICE_URL "/event/" uid "/time-value")
                             {:throw-exceptions false
                              :as :json
                              :headers {"Authorization" (str "Bearer " token)}})]
      (tap> "GOT EVENT TIME VALUE ------->")
      (tap> response)
      (case (:status response)
        200 (response/response (:value (:body response)))
        401 (response/status 401 {:error "Unauthorized"})
        404 (response/not-found {:error "No events found"
                                 :details (:body response)})
        (do
          (response/status 500 {:error "Failed to fetch events data"}))))))


(defn get-event-time [uid token]
  (tap> "GETTING EVENT TIME from CLARITY")
  (tap> token)
  (try
    (let [response (http/get (str CLARITY_SERVICE_URL "/event/" uid "/time")
                             {:throw-exceptions false
                              :as :json
                              :headers {"Authorization" (str "Bearer " token)}})]
      (tap> "GOT EVENT TIME ------->")
      (tap> response)
      (case (:status response)
        200 (response/response (:value (:body response)))
        401 (response/status 401 {:error "Unauthorized"})
        404 (response/not-found {:error "No events found"
                                 :details (:body response)})
        (do
          (response/status 500 {:error "Failed to fetch events data"}))))))

(defn get-event-participants [uid token]
  (tap> "GETTING EVENT PARTICIPANTS from CLARITY")
  (tap> token)
  (try
    (let [response (http/get (str CLARITY_SERVICE_URL "/event/" uid "/participants")
                             {:throw-exceptions false
                              :as :json
                              :headers {"Authorization" (str "Bearer " token)}})]
      (tap> "GOT EVENT PARTICIPANTS ------->")
      (tap> response)
      (case (:status response)
        200 (response/response (:value (:body response)))
        401 (response/status 401 {:error "Unauthorized"})
        404 (response/not-found {:error "No events found"
                                 :details (:body response)})
        (do
          (response/status 500 {:error "Failed to fetch events data"}))))))
