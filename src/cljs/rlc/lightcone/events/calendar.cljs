(ns rlc.lightcone.events.calendar
  (:require [re-frame.core :as rf]
            [rlc.lightcone.api.calendar :as api]
            [cljs.core.async :refer [go <!]]))

;; Events
(rf/reg-event-fx
 :calendar/fetch-events
 (fn [{:keys [db]} _]
   {:db (assoc db :loading? true)
    :calendar/fetch-events nil}))

(rf/reg-event-fx
 :calendar/save-event
 (fn [{:keys [db]} [_ event]]
   (js/console.log "Saving event:" (clj->js event))
   {:calendar/save-event event}))

(rf/reg-event-fx
 :calendar/begin-edit-event
 (fn [{:keys [db]} [_ event]]
   (js/console.log "begin edit event:" (clj->js event))
   {:calendar/begin-edit-event event}))

(rf/reg-event-fx
 :calendar/update-event
 (fn [{:keys [db]} [_ event]]
   {:calendar/update-event event}))

(rf/reg-event-fx
 :calendar/update-event-title
 (fn [{:keys [db]} [_ event]]
   {:calendar/update-event-title event}))

(rf/reg-event-fx
 :calendar/update-event-note
 (fn [{:keys [db]} [_ event]]
   {:calendar/update-event-note event}))

(rf/reg-event-fx
 :calendar/update-event-participants
 (fn [{:keys [db]} [_ event]]
   {:calendar/update-event-participants event}))

(rf/reg-event-fx
 :calendar/delete-event
 (fn [{:keys [db]} [_ id]]
   {:calendar/delete-event id}))

;;;;;;;;;;;;;;;;

(rf/reg-event-db
 :calendar/events-loaded
 (fn [db [_ events]]
   (-> db
       (assoc :events events)
       (assoc :loading? false))))

(rf/reg-event-db
 :calendar/event-updated
 (fn [db [_ {event :event}]]
   (js/console.log "EVENT UPDATED:" (clj->js event))
   (js/console.log "FOOBABAZQUXQUUX"
                   (clj->js (update db :events #(mapv
                                                 (fn [e]
                                                   (if (= (:uid e) (:uid event))
                                                     event
                                                     e))
                                                 %))))

   (js/console.log "Event updated:" (clj->js event))
    (-> db
        (update :events #(mapv
                            (fn [e]
                              (if (= (:uid e) (:uid event))
                                event
                                e))
                            %)))
   ))

(rf/reg-event-db
 :calendar/event-participants-updated
 (fn [db [_ id body]]
   (js/console.log "EVENT PARTICIPANTS UPDATED:" (clj->js body))
   (js/console.log "FOOBABAZQUXQUUX"
                   (clj->js (update db :events #(mapv
                                                 (fn [e]
                                                   (if (= (:uid e) id)
                                                     (assoc e :participants body)  ;; Update just the participants field
                                                     e))
                                                 %))))

   (js/console.log "Event participants updated:" (clj->js body))
   (-> db
     (update :events #(mapv
                          (fn [e]
                            (if (= (:uid e) id)
                              (assoc e :participants body)
                              e))
                          %)))
   ))

(rf/reg-event-db
 :calendar/event-title-updated
 (fn [db [_ id title]]
   (js/console.log "EVENT TITLE UPDATED:" (clj->js title))
   (js/console.log "FOOBABAZQUXQUUX"
                   (clj->js (update db :events #(mapv
                                                 (fn [e]
                                                   (if (= (:uid e) id)
                                                     (assoc e :title title)  ;; Update just the title field
                                                     e))
                                                 %))))

   (js/console.log "Event title updated:" (clj->js title))
   (-> db
       (update :events #(mapv
                         (fn [e]
                           (if (= (:uid e) id)
                             (assoc e :title title)
                             e))
                         %)))
   ))

(rf/reg-event-db
 :calendar/event-note-updated
 (fn [db [_ id note]]
   (js/console.log "EVENT NOTE UPDATED:" (clj->js note))
   (js/console.log "FOOBABAZQUXQUUX"
                   (clj->js (update db :events #(mapv
                                                 (fn [e]
                                                   (if (= (:uid e) id)
                                                     (assoc e :note note)  ;; Update just the note field
                                                     e))
                                                 %))))

   (js/console.log "Event note updated:" (clj->js note))
   (-> db
       (update :events #(mapv
                         (fn [e]
                           (if (= (:uid e) id)
                             (assoc e :note note)
                             e))
                         %)))
   ))

;; Add event-saved handler
(rf/reg-event-db
 :calendar/event-saved
 (fn [db [_ {event :event}]]
   (js/console.log "Event saved:" (clj->js event))
   (-> db
       (update :events conj event))))

;; Add event-deleted handler
(rf/reg-event-db
 :calendar/event-deleted
 (fn [db [_ id]]
   (js/console.log "Deleting event with id:" id)
   (update db :events #(filterv (fn [e] (not= (:id e) id)) %))))

(defn get-event [db event-uid]
  (->> (:events db)
       (filter #(= (:uid %) event-uid))
       first))

(rf/reg-event-db
 :calendar/begin-edit-event
 (fn [db [_ event-uid]]
   (let [event (get-event db event-uid)]
     (assoc db :form event)
     ))
 )

;;;;;;;;;;;;;;;;;;;;;;; Effects

(rf/reg-fx
 :calendar/fetch-events
 (fn [_]
   (go (when-let [events (<! (api/fetch-events))]
         (rf/dispatch [:calendar/events-loaded events])))))

(rf/reg-fx
 :calendar/save-event
 (fn [event]
   (js/console.log "Save event effect triggered with:" (clj->js event))
   (go (try
         (let [saved (<! (api/save-event! event))]
           (if saved
             (rf/dispatch [:calendar/event-saved saved])
             (rf/dispatch [:error/api {:message "Failed to save event"}])))
         (catch js/Error e
           (rf/dispatch [:error/api e]))))))


(rf/reg-fx
 :calendar/update-event
 (fn [event]
   (js/console.log "Update event effect triggered with:" (clj->js event))
   (go (try
         (let [updated (<! (api/update-event! event))]
           (if updated
             (rf/dispatch [:calendar/event-updated {:event updated}])
             (rf/dispatch [:error/api {:message "Failed to update event"}])))
         (catch js/Error e
           (rf/dispatch [:error/api e]))))))

(rf/reg-fx
 :calendar/update-event-title
 (fn [event]
   (go (try
         (js/console.log "Update event title effect triggered with:" (clj->js event))
         (let [updated (<! (api/update-event-title! (:uid event) (:title event)))]
            (js/console.log "Updated event title:" (clj->js (:body updated)))
            (rf/dispatch [:calendar/event-title-updated (:uid event) (:title (:body updated))])
           )
         (catch js/Error e
           (rf/dispatch [:error/api e]))))))

(rf/reg-fx
 :calendar/update-event-note
 (fn [event]
   (go (try
         (js/console.log "Update event note effect triggered with:" (clj->js event))
         (let [updated (<! (api/update-event-note! (:uid event) (:note event)))]
           (js/console.log "Updated event note:" (clj->js (:full_definition (:body updated))))
            (rf/dispatch [:calendar/event-note-updated (:uid event) (:full_definition (:body updated))])
           )
         (catch js/Error e
           (rf/dispatch [:error/api e]))))
   ))

(rf/reg-fx
 :calendar/update-event-participants
 (fn [{:keys [uid participants]}]
   (js/console.log "Update event participants effect triggered with:" (clj->js {:uid uid :participants participants}))
   (go (try
         (let [updated (<! (api/update-event-participants! uid participants))]
           (js/console.log "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Updated event:" (clj->js (:body updated)))
           (if updated
             (rf/dispatch [:calendar/event-participants-updated uid (:body updated)])
             (rf/dispatch [:error/api {:message "Failed to update event"}])))
         (catch js/Error e
           (rf/dispatch [:error/api e]))))))

;; (rf/reg-fx
;;  :calendar/update-event-time
;;  (fn [event]
;;    (go (try
;;          (js/console.log "update event time effect triggered with:" (clj->js event))
;;           (let [updated (<! (api/update-event-time! (:uid event) (:time event)))]
;;             (if updated
;;               (rf/dispatch [:calendar/event-updated updated])
;;               (rf/dispatch [:error/api {:message "Failed to update event"}])))
;;          (catch js/Error e
;;            (rf/dispatch [:error/api e]))))))

(rf/reg-fx
 :calendar/delete-event
 (fn [id]
   (go (try
         (let [response (<! (api/delete-event! id))]
           (if (= true response)
             (rf/dispatch [:calendar/event-deleted id])
             (rf/dispatch [:error/api response])))
         (catch js/Error e
           (rf/dispatch [:error/api e]))))))
