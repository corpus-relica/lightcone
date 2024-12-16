(ns rlc.lightcone.io.calendar
  (:require [clojure.spec.alpha :as s]
            [rlc.lightcone.io.contacts :refer  [get-person-name]]
            [rlc.lightcone.io.archivist :refer [reserve-uid
                                             submit-binary-facts
                                             ;; get-event
                                             ;; get-all-events
                                             ;; get-event-time
                                             ;; get-event-time-value
                                             ;; get-event-participants
                                             get-event-note
                                             get-event-note-value
                                             get-participation-fact
                                             add-participants
                                             remove-participants
                                             post-blanket-rename
                                             delete-facts
                                             update-definition]]
             [rlc.lightcone.io.clarity :as clarity :refer [get-event
                                                           get-all-events
                                                           ;; get-event-time
                                                           ;; get-event-time-value
                                                           ;; get-event-participants
                                                           ]]))

;; Domain level specs
(s/def ::uid int?)
(s/def ::event-type #{:event})
(s/def ::title string?)
(s/def ::note string?)
(s/def ::participants (s/coll-of string?))
(s/def ::location string?)

(s/def ::calendar-event
  (s/keys :req-un [::uid
                   ::event-type
                   ::title
                   ::time    ; Not directly an aspect/point-in-time
                   ]
          :opt-un [::note
                   ::location
                   ::participants]))

(comment
  (def some-event
    {:uid 1
     :event-type :event
     :title "generic event"
     :time #inst "2020-01-01T10:00:00.000-00:00"
     :note "Some description"
     :location "Some location"
     :participants ["Alice" "Bob"]})

  (s/explain ::calendar-event some-event))

(def event-kind-ref 1000000395)
(def period-in-time-kind-ref 2064)
(def point-in-time-kind-ref 550023)


(defn ->point-in-time
  "Transform a time to a point-in-time aspect"
  [timestamp uid possessor-uid]
  {:uid uid
   :name (str "Time at " timestamp)
   :nature :individual
   :kind-ref point-in-time-kind-ref
   :possessor possessor-uid  ; critical - the period possesses its time points
   :aspect-nature :quantitative
   :value timestamp
   :uom :iso8601})


(def person-kind-ref 990010)
(def involvement-as-attendee-kind-ref 1000000685)
(def involvement-as-participant-kind-ref 5644)


(defn participant->involvement
  "Transform an attendee to an involvement tuple"
  [participant]
  (let [person-result (get-person-name participant)
        person-name (:body person-result)]
    [involvement-as-participant-kind-ref
     {:uid participant ;; or retrieve from semantic model
      :name person-name
      :nature :individual
      :kind-ref person-kind-ref}]))

(def possession-of-aspect-kind-ref 1727)
(def note-kind-ref 1000000035)
(def location-of-an-occurrence-kind-ref 1786)
(def spacial-aspect-kind-ref 1355)

(defn location->involvement
  "Transform a location to an involvement tuple"
  [location]
  [location-of-an-occurrence-kind-ref
   {:uid (reserve-uid 1) ;; or retrieve as special aspect of some physical object in the semantic model
    :name location
    :nature :individual
    :kind-ref spacial-aspect-kind-ref}])

(defn note->aspect
  "Transform a note to an aspect tuple"
  [event-title note]
  [possession-of-aspect-kind-ref
   {:uid (first (reserve-uid 1)) ;; or retrieve as special aspect of some physical object in the semantic model
    :name (str "Note for " event-title)
    :nature :individual
    :aspect-nature :qualitative
    :value note
    :kind-ref note-kind-ref}])

;; Transform functions between domains
(defn calendar->clarity
  "Transform calendar event to clarity model"
  [{:keys [event-type title time note uid] :as event}]
  (tap> "CALENDAR->CLARITY")
  (tap> [event-type title time note uid])
  (tap> (:participants event))
  (let [reserved-uids (reserve-uid 2)
        event-uid (if (> uid 100) uid (first reserved-uids))
        point-uid (second reserved-uids)]
    (tap> "RESERVED UIDS")
    (tap> reserved-uids)
    {:uid event-uid
     :name title
     :nature :individual
     :kind-ref event-kind-ref ; Map to appropriate kind
     :occurrence-type :event
     :is-the-case-at (->point-in-time time point-uid event-uid)
     :involved (cond-> []
                 (:participants event)
                 (into (map participant->involvement (:participants event)))
                 (:location event)
                 (conj (location->involvement (:location event))))
     :aspects (cond-> []
                (:note event)
                (conj (note->aspect title (:note event))))}))

(defn clarity->calendar
  "Transform clarity model to calendar event"
  [{:keys [uid name time] :as clarity-event}]
  (tap> "CLARITY->CALENDAR")
  (tap> [uid name time])
  (tap> (:involved clarity-event))
  (tap> (filter #(= involvement-as-participant-kind-ref (first %)) (:involved clarity-event)))
  {:uid uid
   :title name
   :time (-> clarity-event :is-the-case-at :value)
   ;;:note
   :participants (map
                  #(-> (second %) (:uid))
                  (filter
                   #(= involvement-as-participant-kind-ref (first %))
                   (:involved clarity-event)))})
   ;; :location (-> clarity-event :involved
   ;;               (filter #(= location-of-an-occurrence-kind-ref (:kind-ref (second %))))
   ;;               (first :name))})


(defn gellish-point-in-time-aspect-def [point-in-time]
  (let [point-in-time-uid (:uid point-in-time)
        point-in-time-name (:name point-in-time)
        point-in-time-value (:value point-in-time)
        gellish-point-in-time-aspect [{:lh-object-uid point-in-time-uid
                                       :lh-object-name point-in-time-name
                                       :rel-type-uid 1225
                                       :rel-type-name "is classified as a"
                                       :rh-object-uid point-in-time-kind-ref
                                       :rh-object-name "point in time"}
                                      {:lh-object-uid point-in-time-uid
                                       :lh-object-name point-in-time-name
                                       :rel-type-uid 5025
                                       :rel-type-name "has on scale as value"
                                       ;; point-in-time-value to unix epoch
                                       :rh-object-uid (-> (java.time.Instant/parse point-in-time-value)
                                                          .getEpochSecond)
                                       :rh-object-name point-in-time-value
                                       :uom-uid 570779
                                       :uom-name "UTC"}]]
    gellish-point-in-time-aspect))

(defn gellish-possession-of-point-in-time-relation-def [clarity-event]
  (let [event-uid (:uid clarity-event)
        event-name (:name clarity-event)
        point-in-time (:is-the-case-at clarity-event)
        point-in-time-uid (:uid point-in-time)
        point-in-time-name (:name point-in-time)
        gellish-possession-of-point-in-time-relation {:lh-object-uid event-uid
                                                      :lh-object-name event-name
                                                      :rel-type-uid 1785
                                                      :rel-type-name "is the case at time"
                                                      :rh-object-uid point-in-time-uid
                                                      :rh-object-name point-in-time-name}]
    gellish-possession-of-point-in-time-relation))

(defn gellish-participation-relations-def [clarity-event]
  (let [event-uid (:uid clarity-event)
        event-name (:name clarity-event)
        involved (:involved clarity-event)
        gellish-participation-relations (map (fn [[uid entity]]
                                               {:lh-object-uid event-uid
                                                :lh-object-name event-name
                                                :rel-type-uid 5644
                                                :rel-type-name "has as participant"
                                                :rh-object-uid (:uid entity)
                                                :rh-object-name (:name entity)})
                                             involved)]
    gellish-participation-relations))

(defn gellish-note-relations-def [clarity-event]
  (let [event-uid (:uid clarity-event)
        event-name (:name clarity-event)
        aspects (:aspects clarity-event)
        gellish-note-relations (let [note-aspect (second (first aspects))
                                     note-uid (:uid note-aspect)
                                     note-name (str "Note for - " event-name)
                                     note-value (:value note-aspect)
                                     note-kind-ref 1000000035]
                                 [{:lh-object-uid note-uid
                                   :lh-object-name note-name
                                   :rel-type-uid 1225
                                   :rel-type-name "is classified as a"
                                   :rh-object-uid note-kind-ref
                                   :rh-object-name "note"
                                   :partial-definition note-value
                                   :full-definition note-value}
                                  {:lh-object-uid event-uid
                                   :lh-object-name event-name
                                   :rel-type-uid possession-of-aspect-kind-ref
                                   :rel-type-name "has as aspect"
                                   :rh-object-uid note-uid
                                   :rh-object-name note-name}])]
    gellish-note-relations))

(defn save-to-storage [clarity-event]
  ;; Save to your storage
  ;; For example, a vector in an atom
  (tap> "SAVE TO STORAGE")
  (tap> clarity-event)

  ;; create the event in the semantic model
  (let [gellish-event {:lh-object-uid (:uid clarity-event)
                       :lh-object-name (:name clarity-event)
                       :rel-type-uid 1225
                       :rel-type-name "is classified as a"
                       :rh-object-uid event-kind-ref
                       :rh-object-name "event"
                       :partial-definition (:note clarity-event)
                       :full-definition (:note clarity-event)}
        point-in-time (:is-the-case-at clarity-event)
  ;; find or create the point-in-time aspect
  ;; create the is-the-case-at relation
  ;; find the involved entities
  ;; create the participation relations
        gellish-point-in-time-aspect (gellish-point-in-time-aspect-def point-in-time)
        gellish-possession-of-point-in-time-relation (gellish-possession-of-point-in-time-relation-def clarity-event)
        gellish-participation-relations (gellish-participation-relations-def clarity-event)
        gellish-note-relations (gellish-note-relations-def clarity-event)
        gellish-facts (concat [gellish-event]
                              gellish-point-in-time-aspect
                              [gellish-possession-of-point-in-time-relation]
                              gellish-participation-relations
                              gellish-note-relations)
        result (submit-binary-facts gellish-facts)]
    (tap> "GELLISH EVENT")
    (tap> gellish-facts)
    ;; (tap> "SAVE EVENT")
    ;; (tap> gellish-facts)
    (tap> result)

    clarity-event))

(defn fetch-all-events [token]
  (tap> "FETCH ALL EVENTS OUTER")
  (let [events (:body (get-all-events token))
        ;; Use mapv to force sequential processing
        _ (tap> "FETCH ALL EVENT TIMES")
        times (doall (mapv #(try
                              (let [fonk (clarity/get-event-time-value (:uid %) token)]
                                (tap> fonk)
                                (:body fonk))
                              (catch Exception e
                                (tap> (str "Failed to get time for " (:uid %)))
                                nil))
                           events))
        _ (tap> "FETCH ALL EVENTS")
        ;; Add delay between calls if needed
        ;; _ (Thread/sleep 100)
        ;; participants (doall (mapv #(try
        ;;                              (:body (get-event-participants token (:uid %)))
        ;;                              (catch Exception e
        ;;                                (tap> (str "Failed to get participants for " (:uid %)))
        ;;                                nil))
        ;;                           events))
        _ (tap> "FETCH ALL PARTICIPANTS")
        ;; _ (tap> participants)
        ;; notes (doall (mapv #(try
        ;;                       (:body (get-event-note-value token (:uid %)))
        ;;                       (catch Exception e
        ;;                         (tap> (str "Failed to get note for " (:uid %)))
        ;;                         nil))
        ;;                    events))
        ;; final-events (map #(assoc %1 :time %2 :participants %3 :note %4) events times participants notes)
        final-events (map #(assoc %1 :time %2) events times)
        ]
    (tap> "FETCH ALL EVENTS INNER : FINAL EVENTS")
    ;; (tap> times)
    ;; (tap> events)
    (tap> final-events)
    final-events))

(defn fetch-event [token uid]
  (let [event (:body (get-event token uid))
        time (try
               (:body (clarity/get-event-time-value token uid))
               (catch Exception e
                 (tap> (str "Failed to get time for " uid))
                 nil))
        participants (try
                       (:body (clarity/get-event-participants token uid))
                       (catch Exception e
                         (tap> (str "Failed to get participants for " uid))
                         nil))
        note (try
               (:body (get-event-note-value token uid))
               (catch Exception e
                 (tap> (str "Failed to get note for " uid))
                 nil))]
    (tap> "FETCH EVENT WTF?")
    (tap> uid)
    (tap> event)
    (tap> time)
    (tap> participants)
    (tap> (assoc event :time time :participants participants :note note))
    (assoc event :time time :participants participants :note note)))

(defn create-event [calendar-event]
  (tap> "CREATE EVENT")
  (tap> calendar-event)

  ;; Transform simple calendar event to your model
  (let [clarity-event (-> calendar-event
                          calendar->clarity)]
    (tap> "CLAIRTY EVENT")
    (tap> clarity-event)
    (save-to-storage clarity-event)
    ;; Return a simplified view for the UI
    (clarity->calendar clarity-event)))

(defn update-event [id calendar-update]
  (tap> "UPDATE EVENT")
  (tap> [id calendar-update])

  ;; Transform simple calendar event to your model
  (let [calendar-event (fetch-event id)
        _ (tap> "CALENDAR EVENT")
        _ (tap> calendar-event)
        updated-event (-> calendar-event
                          (merge calendar-update)
                          calendar->clarity)
        _ (tap> "UPDATED EVENT")
        _ (tap> updated-event)
        foobar (get-event-time id)
        facts-to-delete (map (fn [fact]
                               (:fact_uid fact))
                             (:body foobar))
        ;; delete the facts sequentially
        _ (tap> "FACTS TO DELETE")
        _ (tap> facts-to-delete)

        deleted-facts (delete-facts facts-to-delete)
        _ (tap> "DELETED FACTS")
        _ (tap> deleted-facts)

        xxx (gellish-point-in-time-aspect-def (:is-the-case-at updated-event))
        _ (tap> "XXX")
        _ (tap> xxx)
        yyy (gellish-possession-of-point-in-time-relation-def updated-event)
        _ (tap> "YYY")
        _ (tap> yyy)
        result (submit-binary-facts (concat xxx [yyy]))

        ;; create new event aspect
        ;; create new aspect value
        ;; relate the new aspect value to the new event aspect
        ;; relate the new event aspect to the entity
        ]

    ;; Return calendar format for UI
    (clarity->calendar updated-event)))

(defn update-event-note
  "Update the note of an event"
  [event-id note]
  (tap> "************************************************************  UPDATE EVENT NOTE")
  (tap> [event-id note])

  (let [event-note (get-event-note event-id)
        fact-uid (-> event-note :body second :fact_uid)
        _ (tap> "EVENT NOTE FACT!!!!")
        _ (tap> fact-uid)
        result (update-definition fact-uid note)]
    (tap> "RESULT")
    (tap> result)
    result))

(defn update-event-field [event-id field value]
  (tap> "UPDATE EVENT FIELD")
  (tap> [event-id field value])

  (let [result (post-blanket-rename event-id value)
        event (get-event event-id)]
    (tap> "RESULT")
    (tap> result)
    (tap> "EVENT")
    (tap> (:body event))
    (:body event))
 ;; (let [event (get-event event-id)
 ;;       updated (assoc event field value)]
 ;;   (save-to-storage updated)
 ;;   updated)
  )

(defn update-event-participants [event-id field value]
  (let [target-participiants (set value)
        model-participants (set (:body (get-event-participants event-id)))
        participant-ids-to-add (clojure.set/difference target-participiants model-participants)
        participant-ids-to-remove (clojure.set/difference model-participants target-participiants)
        added-participants (add-participants event-id participant-ids-to-add)
        removed-participants (remove-participants event-id participant-ids-to-remove)]
    ;; (tap> "SEMANTIC MODEL PARTICIPANTS")
    ;; (tap> "to add")
    ;; (tap> participant-ids-to-add)
    ;; (tap> "to remove")
    ;; (tap> participant-ids-to-remove)
    ;; (tap> "added")
    ;; (tap> added-participants)
    ;; (tap> "removed")
    ;; (tap> removed-participants)
    )

  ;; get existing participants
  ;; add new participants
  ;; remove old participants
  ;; save to storage
  ;; return updated event
  )

(defn delete-event [id]
  (tap> "DELETE EVENT")
  (tap> id)
  id)

(comment
  (tap> (calendar->clarity some-event))
  (s/explain :rlc.clarity.occurrence/occurrence (calendar->clarity some-event)))
