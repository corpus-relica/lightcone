(ns rlc.lightcone.routes.events
  (:require [compojure.core :refer [defroutes context GET POST PUT DELETE]]
            [ring.util.response :as response]
            [clojure.walk :refer [keywordize-keys]]
            [rlc.lightcone.calendar :as calendar]
            [rlc.lightcone.auth :as auth]))

(defn extract-token [request]
  (let [headers (:headers request)
        header-value (get headers "authorization")]
    (if header-value
      (second (clojure.string/split header-value #" "))
      nil)))

(defroutes event-routes
  (context "/api/event/title" []
    (PUT "/:id" {params :params body :body}
      (let [event-id (Integer/parseInt (:id params))
            new-title (:title (keywordize-keys body))
            updated-event (calendar/update-event-field event-id :title new-title)]
        (response/response updated-event))))

  (context "/api/event/note" []
    (PUT "/:id" {params :params body :body}
      (tap> "UPDATE NOTE")
      (tap> params)
      (tap> body)
      (let [event-id (Integer/parseInt (:id params))
            new-note (:note (keywordize-keys body))

            updated-event (calendar/update-event-note event-id new-note)]
        (response/response updated-event))))

  (context "/api/event/participants" []
    (PUT "/:id" {params :params body :body}
      (let [event-id (Integer/parseInt (:id params))
            new-participants (:participants (keywordize-keys body))
            updated-event (calendar/update-event-participants event-id :participants new-participants)]
        (tap> "NEW PARTICIPANTS")
        (tap> [event-id new-participants])
        (response/response (set new-participants)))))

  ;; (context "/api/events" []
  ;;   (GET "/" []
  ;;     (response/response
  ;;       {:events (calendar/fetch-all-events)}))

  (context "/api/events" []
    (GET "/" request
      (tap> "Auth result:")  ; Debug log
      (let [token (extract-token request)
            auth-result (auth/is-authenticated? token)]
        (tap> token)
        (tap> auth-result)   ; Debug log
        (if (map? auth-result)  ; Ensure we're working with a map
          (cond
            (:authenticated auth-result)
            (response/response (calendar/fetch-all-events token))

            (= (:reason auth-result) :token-expired)
            (response/status 419)

            :else
            (response/status 401))
        ;; Handle case where auth-result isn't a map
          (do
            (tap> "Invalid auth-result type:")
            (tap> (type auth-result))
            (response/status 500))))))

    ;; (PUT "/" {body :body}
    ;;   (let [calendar-event (:event (keywordize-keys body))
    ;;         saved-event (calendar/update-event calendar-event)]
    ;;     (response/response {:event saved-event})))

  (POST "/" {:keys [body]}
    (let [calendar-event (:event (keywordize-keys body))
          saved-event (calendar/create-event calendar-event)]
      (response/response {:event saved-event})))

  (context "/:id" [id]
    (PUT "/" {body :body}
      (let [event-id (Integer/parseInt id)
            calendar-update (:event (keywordize-keys body))
            saved-event (calendar/update-event event-id calendar-update)]
        (response/response saved-event)))

    (DELETE "/" []
      (let [event-id (Integer/parseInt id)]
        (calendar/delete-event event-id)
        (response/response {:status "ok"})))))
