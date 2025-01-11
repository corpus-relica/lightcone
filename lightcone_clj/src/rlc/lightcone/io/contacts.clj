(ns rlc.lightcone.io.contacts
  (:require [clj-http.client :as http]
            [ring.util.response :as response]
            ;; [clojure.tools.logging :as log]
            [rlc.lightcone.env :refer [ARCHIVIST_SERVICE_URL
                                       CLARITY_SERVICE_URL]]
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


(defn create-person [token body]
  (client/auth-post
    (str ARCHIVIST_SERVICE_URL "/fact/fact")
    token
    {:lh_object_uid (:uid body)
     :lh_object_name (:name body)
     :rel_type_uid 1225
     :rel_type_name "is classified as a"
     :rh_object_uid 990010
     :rh_object_name "person"}
    {:transform-fn (fn [response-body]
                     (let [fact (:fact response-body)]
                       {:uid (:lh_object_uid fact)
                        :name (:lh_object_name fact)}))}))

(defn update-person [token body]
  (client/auth-put
    (str ARCHIVIST_SERVICE_URL "/fact")
    token
    body))

(defn delete-person [token uid]
  (tap> "DELETING PERSON @ CLARITY")
  (client/auth-delete
    (str CLARITY_SERVICE_URL "/physical-object/" uid)
    token))

;; (defn update-person [token body]
;;   (client/auth-put
;;     (str ARCHIVIST_SERVICE_URL "/fact")
;;     token
;;     {:body (json/write-str body)
;;      :content-type :json}))

;; (defn delete-person [token uid])
;;   (client/auth-delete
;;     (str ARCHIVIST_SERVICE_URL "/fact")
;;     token
;;     {:query-params {:uid uid}})
