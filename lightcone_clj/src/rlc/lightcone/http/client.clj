(ns rlc.lightcone.http.client
  (:require [clj-http.client :as http]
            [ring.util.response :as response]))

(def default-timeout 5000)

(def default-options
  {:throw-exceptions false
   :as :json
   :connection-timeout default-timeout
   :socket-timeout default-timeout})

(defn make-auth-headers [token]
  {"Authorization" (str "Bearer " token)})

(defn auth-get
  "Make authenticated GET request with optional query params and response transformation
   Options map can include:
   :query-params - map of query parameters
   :transform-fn - function to transform successful response body"
  ([url token]
   (auth-get url token {}))
  ([url token {:keys [query-params transform-fn]
               :or {transform-fn identity}}]
   (try
     (let [request-options (merge default-options
                                {:headers (make-auth-headers token)}
                                (when query-params
                                  {:query-params query-params}))
           response (http/get url request-options)]
       (case (:status response)
         200 (response/response (transform-fn (:body response)))
         401 (response/status 401 {:error "Unauthorized"})
         404 (response/not-found {:error "Resource not found"
                                :details (:body response)})
         (do
           (tap> (str "Failed with status: " (:status response)))
           (response/status 500 {:error "Request failed"}))))
     (catch Exception e
       (tap> "HTTP Request failed")
       (tap> (.getMessage e))
       (response/status 500 {:error "Service connection error"})))))

;; Add other HTTP methods
(defn auth-post [url token body]
  ;; Similar to auth-get but with POST
  )

(defn auth-put [url token body]
  ;; Similar pattern for PUT
  )

;; Add request options merging
(defn with-options [base-options custom-options]
  (merge-with merge base-options custom-options))
