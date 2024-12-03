(ns rlc.lightcone.views.event-form
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [re-com.core :as rc]
            [re-com.dropdown :refer [single-dropdown]]
            [re-com.datepicker :refer [datepicker-dropdown]]
            [goog.date.UtcDateTime]
            [re-com.input-time :refer [input-time]]
  ))

;; Helper functions for time conversion
(defn utc->time-str
  "Convert UTC timestamp to HH:mm format"
  [utc-str]
  (when utc-str
    (let [date (js/Date. utc-str)]
      (str (-> (.getUTCHours date)
               (.toString)
               (#(if (< (count %) 2) (str "0" %) %)))
           ":"
           (-> (.getUTCMinutes date)
               (.toString)
               (#(if (< (count %) 2) (str "0" %) %)))))))

(defn utc->time-num
  "Convert UTC timestamp to re-com.input-time model: a time in integer form. e.g. '09:30am' is 930"
  [utc-str]
  (when utc-str
    (let [date (js/Date. utc-str)]
      (+ (* 100 (.getUTCHours date))  ; Use .getUTCHours
         (.getUTCMinutes date)))))   ; Use .getUTCMinutes

(defn time-num->utc
  "Convert re-com.input-time model to UTC timestamp"
  [time-num date-str]
  (when (and time-num date-str)
    (let [hours (quot time-num 100)
          minutes (mod time-num 100)
          date (js/Date. date-str)]
      (.setHours date hours)
      (.setMinutes date minutes)
      (.toISOString date))))

(defn time-str->utc
  "Convert HH:mm to UTC timestamp, preserving the current date"
  [time-str date-str]
  (when (and (string? time-str) (string? date-str))
    (let [[hours minutes] (map js/parseInt (clojure.string/split time-str #":"))
          date (js/Date. date-str)] ; Ensures input is interpreted as UTC
      (.setUTCHours date hours)
      (.setUTCMinutes date minutes)
      (println "Setting time to UTC:" hours ":" minutes "and date is" date date-str)
      (.toISOString date))))

(defn js-date->goog-date [js-date]
  (when js-date
    (doto (goog.date.UtcDateTime.)
      (.setTime (.getTime js-date)))))

;; And converting back
(defn goog-date->js-date [goog-date]
  (when goog-date
    (js/Date. (.getTime goog-date))))

(defn new-event-form []
  (let [form @(rf/subscribe [:form/fields])
        selected-date (r/atom (when (:time form)
                               (js-date->goog-date (js/Date. (:time form)))))
        ;; Convert participants to a set if it's not already
        participants (if (sequential? (:participants form))
                      (set (:participants form))
                      (:participants form #{}))
        people @(rf/subscribe [:people])]
    (js/console.log "Rendering form with:" (clj->js form) "and date:" @selected-date)
    (js/console.log "People are:" (clj->js people))
    (js/console.log "Participants are:" (clj->js participants))
    (js/console.log "PEOPLE MODEL:" (clj->js (map #(hash-map :id (:id %) :label (:name %) :background-color "#84b6eb") people)))
    [rc/v-box
     :style {:padding "20px"
             :background-color "#f5f5f5"
             :border "1px solid #ddd"
             :border-radius "4px"}
     :gap "20px"
     :children
     [[rc/title
       :label "New Event"
       :level :level2]

      [rc/h-box  ;; Title
       :gap "10px"
       :align :center
       :children
       [[rc/label :label "Title"]
        [rc/input-text
         :model (:title form "")
         :width "300px"
         :placeholder "Enter event title"
         :on-change #(rf/dispatch [:form/set-field :title %])]]]

      [rc/h-box  ;; Date
       :gap "10px"
       :align :center
       :children
       [[rc/label :label "Date"]
        [datepicker-dropdown
         :model selected-date
         :show-today? true
         :on-change #(let [js-date (goog-date->js-date %)
                          date-str (.toISOString js-date)
                          time (or (utc->time-str (:time form)) "00:00")]

                       (js/console.log "Setting date to:" (time-str->utc time date-str))
                       (rf/dispatch [:form/set-field :time (time-str->utc time date-str)])
                      )]]]

      [rc/h-box  ;; Start Time
       :gap "10px"
       :align :center
       :children
       [[rc/label :label "Time"]
        [input-time
         :model (or (utc->time-num (:time form)) 0)
         :on-change #(do
                        (js/console.log "Setting start time to !!!!!!!!!!:" %)
                       (rf/dispatch [:form/set-field :time
                                 (time-num->utc % (or (:time form)
                                                     (.toISOString (js/Date.))))]))
         :show-icon? true
         :style {:width "100px"}]]]

      [rc/h-box  ;; Participants
       :gap "10px"
       :align :center
       :children
       [[rc/label :label "Participants"]
        [rc/tag-dropdown
         :model participants
         :choices (map #(hash-map :id (:id %) :label (:name %) :background-color "#84b6eb") people)
         :on-change #(do
                       (js/console.log "********************** Setting participants to:" %)
                       (rf/dispatch [:form/set-field :participants (set %)]))]
        ;; [rc/input-text
        ;;  :model (if (sequential? (:participants form))
        ;;          (clojure.string/join ", " (:participants form))
        ;;          (:participants form ""))
        ;;  :width "300px"
        ;;  :placeholder "Enter names separated by commas"
        ;;  :on-change #(rf/dispatch [:form/set-field :participants %])]
        ]]

      [rc/h-box  ;; Note
       :gap "10px"
       :align :center
       :children
       [[rc/label :label "Note"]
        [rc/input-textarea
         :model (or (:note form) "")
         :width "300px"
         :placeholder "Enter a brief note"
         :on-change #(rf/dispatch [:form/set-field :note %])]]]

      [rc/h-box  ;; Buttons
       :gap "10px"
       :justify :end
       :children
       [[rc/button
         :label "Clear"
         :class "btn-default"
         :on-click #(rf/dispatch [:form/clear])]
        [rc/button
         :label (if (= 0 (:uid form)) "Create Event" "Update Event")
         :class "btn-primary"
         :on-click #(let [formatted-event (-> form
                                            (update :participants
                                                    (fn [x] x)))]
                     (js/console.log "Saving event:" (clj->js formatted-event))
                     (rf/dispatch (if (= 0 (:uid form))
                                    [:calendar/save-event formatted-event]
                                    [:calendar/update-event formatted-event]))
                     (rf/dispatch [:form/clear])
                     )]]]]]))


(defn edit-event-form []
  (let [form @(rf/subscribe [:form/fields])
        selected-date (r/atom (when (:time form)
                               (js-date->goog-date (js/Date. (:time form)))))
        ;; Convert participants to a set if it's not already
        participants (if (sequential? (:participants form))
                      (set (:participants form))
                      (:participants form #{}))
        people @(rf/subscribe [:people])]
    (js/console.log "Rendering form with:" (clj->js form) "and date:" @selected-date)
    (js/console.log "People are:" (clj->js people))
    (js/console.log "Participants are:" (clj->js participants))
    (js/console.log "PEOPLE MODEL:" (clj->js (map #(hash-map :id (:id %) :label (:name %) :background-color "#84b6eb") people)))
    [rc/v-box
     :style {:padding "20px"
             :background-color "#f5f5f5"
             :border "1px solid #ddd"
             :border-radius "4px"}
     :gap "20px"
     :children
     [[rc/title
       :label "New Event"
       :level :level2]

      [rc/h-box  ;; Title
       :gap "10px"
       :align :center
       :children
       [[rc/label :label "Title"]
        [rc/input-text
         :model (:title form "")
         :width "300px"
         :placeholder "Enter event title"
         :on-change #(do
                       (rf/dispatch [:form/set-field :title %]))]]]

      [rc/h-box  ;; Date
       :gap "10px"
       :align :center
       :children
       [[rc/label :label "Date"]
        [datepicker-dropdown
         :model selected-date
         :show-today? true
         :on-change #(let [js-date (goog-date->js-date %)
                          date-str (.toISOString js-date)
                          time (or (utc->time-str (:time form)) "00:00")]

                       (js/console.log "Setting date to:" (time-str->utc time date-str))
                       (rf/dispatch [:form/set-time-field :time (time-str->utc time date-str)])
                      )]]]

      [rc/h-box  ;; Time
       :gap "10px"
       :align :center
       :children
       [[rc/label :label "Time"]
        [input-time
         :model (or (utc->time-num (:time form)) 0)
         :on-change #(do
                       (rf/dispatch [:form/set-time-field
                                     :time (time-num->utc % (or (:time form)
                                                                (.toISOString (js/Date.))))]))
         :show-icon? true
         :style {:width "100px"}]]]

      [rc/h-box  ;; Participants
       :gap "10px"
       :align :center
       :children
       [[rc/label :label "Participants"]
        [rc/tag-dropdown
         :model participants
         :choices (map #(hash-map :id (:id %) :label (:name %) :background-color "#84b6eb") people)
         :on-change #(do
                       (js/console.log "********************** Setting participants to:" %)
                       (rf/dispatch [:form/set-participants-field :participants (set %)]))]
        ]]

      [rc/h-box  ;; Note
       :gap "10px"
       :align :center
       :children
       [[rc/label :label "Note"]
        [rc/input-textarea
         :model (or (:note form) "")
         :width "300px"
         :placeholder "Enter a brief note"
         :on-change #(rf/dispatch [:form/set-note-field :note %])]]]

      [rc/h-box  ;; Buttons
       :gap "10px"
       :justify :end
       :children
       [[rc/button
         :label "Clear"
         :class "btn-default"
         :on-click #(rf/dispatch [:form/clear])]
        ]]]]))
(defn event-form []
  (let [form @(rf/subscribe [:form/fields])
        new-event? (= 0 (:uid form))]
    (if new-event?
      [new-event-form form]
      [edit-event-form form])))
