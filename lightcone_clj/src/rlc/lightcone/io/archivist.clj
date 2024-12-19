(ns rlc.lightcone.io.archivist
  (:require [clj-http.client :as http]
            [ring.util.response :as response]
            ;; [clojure.tools.logging :as log]
            [clojure.data.json :as json]))

(def ^:private service-url "http://localhost:3000")

(defn reserve-uid [n]
  (try
    (let [response (http/get (str service-url "/uid/reserve-uid")
                             {:query-params {:n n}
                              :throw-exceptions false
                              :as :json})]
      (case (:status response)
        200 (:body response)
        404 (response/not-found {:error "No UIDs found"
                                 :details (:body response)})
        (do
          ;; (log/error "Failed to fetch UIDs:" response)
          (response/status 500 {:error "Failed to fetch UID data"}))))
    (catch Exception e
      ;; (log/error e "Failed to connect to fact retrieval service")
      (response/status 500 {:error "Service connection error"}))))

(defn kebab->snake [s]
  (clojure.string/replace (name s) "-" "_"))

(defn transform-keys [m]
  (reduce-kv (fn [m k v]
               (let [new-k (if (keyword? k)
                             (keyword (kebab->snake k))
                             k)]
                 (assoc m new-k
                        (cond
                          (map? v) (transform-keys v)
                          (sequential? v) (map #(if (map? %)
                                                  (transform-keys %)
                                                  %) v)
                          :else v))))
             {} m))

(defn submit-binary-facts [facts]
  (try
    (tap> "FACTS!!!!!!!!!!!!")
    (let [transformed-facts (map transform-keys facts)  ;; Transform before serializing
          _ (tap> (str "Transformed facts:" (json/write-str transformed-facts)))
          response (http/post (str service-url "/fact/facts")
                              {:body (json/write-str transformed-facts)
                               :content-type :json
                               :throw-exceptions false
                               :as :json})]
      (case (:status response)
        200 (:body response)
        201 (:body response)
        404 (response/not-found {:error "No facts found"
                                 :details (:body response)})
        (do
          (tap> "FAILED TO SUBMIT FACTS")
          (tap> response)
          (response/status 500 {:error "Failed to submit facts"}))))
    (catch Exception e
      (tap> "FAILED TO SUBMIT FACTS EXCEPTION")
      (tap> e)
      (response/status 500 {:error "Service connection error"}))))

(defn delete-facts [uids]
  (tap> "DELETING FACTS")
  (tap> uids)
  (tap> (json/write-str uids))
  (try
    (let [response (http/delete (str service-url "/fact/facts")
                                {:body (json/write-str uids)
                                 :content-type :json
                                 :throw-exceptions false
                                 :as :json})]
      (case (:status response)
        200 (:body response)
        404 (response/not-found {:error "No event found"
                                 :details (:body response)})
        (response/status 500 {:error "Failed to delete facts"})))
    (catch Exception e
      (tap> "FAILED TO DELETE FACTS")
      (tap> e)
      (response/status 500 {:error "Service connection error"}))))

(defn aspects-of-individual-classified-as [indvUID aspectKindUID token]
  (try
    (let [response (http/get (str service-url "/individual/" indvUID "/aspects-classified-as/" aspectKindUID)
                             {:throw-exceptions false
                              :as :json
                              :headers {"Authorization" (str "Bearer " token)}})]
      (case (:status response)
        200 (:body response)
        404 (response/not-found {:error (str "No aspects classified as " aspectKindUID " found for individual " indvUID)
                                 :details (:body response)})
        (do
          ;; (log/error "Failed to fetch UIDs:" response)
          (response/status 500 {:error (str "Failed retrieving aspects classified as " aspectKindUID " of individual " indvUID)}))))
    (catch Exception e
      ;; (log/error e "Failed to connect to fact retrieval service")
      (response/status 500 {:error "Service connection error"}))))

(defn query-service
  "Makes a query to the service and handles common response patterns"
  [token query-string transform-fn]
  (try
    (let [response (http/post (str service-url "/query/queryString")
                              {:body (json/write-str {:queryString query-string})
                               :content-type :json
                               :throw-exceptions false
                               :as :json
                               :headers {"Authorization" (str "Bearer " token)}})]
      (case (:status response)
        (200 201) (-> response
                      :body
                      :facts
                      transform-fn
                      response/response)
        404 (response/not-found {:error "No event found"
                                 :details (:body response)})
        (response/status 500 {:error "Failed to fetch event data"})))
    (catch Exception e
      (tap> "FAILED TO QUERY SERVICE")
      (tap> e)
      (response/status 500 {:error "Service connection error"}))))


(defn get-participation-fact [lh-object-uid rh-object-uid]
  (query-service
   (str lh-object-uid " > 5644 > " rh-object-uid)
   (fn [facts]
     (tap> "PARTICIPATION FACT")
     (tap> facts)
     (first facts))))

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

(defn add-participant [event-uid person-uid]
  ;; (try
  ;;   (let [event-name (get-in (get-event event-uid) [:body :title])
  ;;         person-name (:body (get-person-name person-uid))
  ;;         fact {:lh-object-uid event-uid
  ;;               :lh-object-name event-name
  ;;               :rel-type-uid 5644
  ;;               :rel-type-name "has as participant"
  ;;               :rh-object-uid person-uid
  ;;               :rh-object-name person-name}
  ;;         response (submit-binary-facts [fact])]
  ;;     ;; (tap> "ADD PARTICIPANT RESPONSE")
  ;;     ;; (tap> response)
  ;;     response)
  ;;   (catch Exception e
  ;;     ;; (log/error e "Failed to connect to fact retrieval service")
  ;;     (response/status 500 {:error "Service connection error"})))
  )

(defn add-participants [event-uid person-uids]
  (doall (map #(add-participant event-uid %) person-uids)))

(defn remove-participant [event-uid person-uid]
  (try
    (let [participation-fact-uid (get-in (get-participation-fact event-uid person-uid) [:body :fact_uid])
          response (delete-facts [participation-fact-uid])]
      ;; (tap> "REMOVE PARTICIPANT RESPONSE")
      ;; (tap> (first response))
      (first response))
    (catch Exception e
      (tap> "FAILED TO REMOVE PARTICIPANT")
      (tap> e)
      (response/status 500 {:error "Service connection error"}))))

(defn remove-participants [event-uid person-uids]
  (doall (map #(remove-participant event-uid %) person-uids)))

(defn post-blanket-rename [uid new-name]
  (try
    (let [response (http/put (str service-url "/submission/blanketRename")
                             {:body (json/write-str {:entity_uid uid
                                                     :name new-name})
                              :content-type :json
                              :throw-exceptions false
                              :as :json})]
      (case (:status response)
        200 (:body response)
        404 (response/not-found {:error "No event found"
                                 :details (:body response)})
        (response/status 500 {:error "Failed to rename event"})))
    (catch Exception e
      (tap> "FAILED TO RENAME EVENT")
      (tap> e)
      (response/status 500 {:error "Service connection error"}))))


(defn update-definition [uid def]
  (tap> "UPDATING DEFINITION")
  (tap> uid)
  (tap> def)
  (try
    (let [response (http/put (str service-url "/submission/definition")
                             {:body (json/write-str {:fact_uid uid
                                                     :partial_definition def
                                                     :full_definition def})
                              :content-type :json
                              :throw-exceptions false
                              :as :json})]
      (tap> "UPDATE DEFINITION RESPONSE")
      (tap> (:body response))
      (case (:status response)
        200 (:body response)
        404 (response/not-found {:error "No event found"
                                 :details (:body response)})
        (response/status 500 {:error "Failed to update definition"})))
    (catch Exception e
      (tap> "FAILED TO UPDATE DEFINITION")
      (tap> e)
      (response/status 500 {:error "Service connection error"}))))
