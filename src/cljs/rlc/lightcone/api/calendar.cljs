(ns rlc.lightcone.api.calendar
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [cljs-http.client :as http]
            [cljs.core.async :refer [go <!]]
            ["vis-timeline/standalone" :refer [Timeline DataSet]]
            ))
;;
;; API functions (unchanged)
(defn fetch-events []
  (go (let [response (<! (http/get "/api/events"))]
        (when (= 200 (:status response))
          (:events (:body response))))))

(defn save-event! [event]
  (js/console.log "Making API call to save event:" (clj->js event))
  (go (let [response (<! (http/post "/api/events"
                                   {:json-params {:event event}
                                    :headers {"Content-Type" "application/json"}}))]
        (js/console.log "API response:" (clj->js response))
        (when (= 200 (:status response))
          (:body response)))))

(defn update-event! [event]
  (js/console.log "Making API call to update event:" (clj->js event))
  (go (let [response (<! (http/put (str "/api/events/" (:uid event))
                                  {:json-params {:event event}}))
            updated-event (:body response)]
        (when (= 200 (:status response))
          updated-event))))

(defn update-event-title! [id title]
  (go (let [response (<! (http/put (str "/api/event/title/" id)
                                  {:json-params {:title title}}))]
        (case (:status response)
          200 response
          400 false
          404 false
          500 false
          false))
      ))

(defn update-event-note! [id note]
  (go (let [response (<! (http/put (str "/api/event/note/" id)
                                  {:json-params {:note note}}))]
        (case (:status response)
          200 response
          400 false
          404 false
          500 false
          false))
      ))

(defn update-event-participants! [id participants]
  (go (let [response (<! (http/put (str "/api/event/participants/" id)
                                  {:json-params {:participants participants}}))]
        (case (:status response)
          200 response
          400 false
          404 false
          500 false
          false))
      ))

(defn update-event-time! [id title]
  ;;
  ;; (go (let [response (<! (http/put (str "/api/event/time/" id)
  ;;                                 {:json-params {:title title}}))]
  ;;       (= 200 (:status response)))))
  ;;
  ;; break / dispose of current time aspect
  ;; create new time aspect
  ;; update event with new time aspect
  ;; return updated event
  )

(declare update-event-participants!)
(declare update-event-note!)
(declare delete-event!)

(defn delete-event! [id]
  (go (let [response (<! (http/delete (str "/api/events/" id)))]
        (= 200 (:status response)))))

;; Timeline state management
(def timeline-state (r/atom nil))
(def timeline-data (r/atom nil))

;; Helper to format events for vis.js
;; (defn format-events-for-vis [events]
;;   (clj->js (map (fn [event]
;;                   {:id (:id event)
;;                    :content (:title event)
;;                    :start (:start event)
;;                    :end (:end event)})
;;                 events)))

;; (declare app-state)

;; (defn init-timeline! [dom-node events]
;;   (js/console.log "INIT TIMELINE")
;;   (js/console.log "DOM node:" dom-node)
;;   (js/console.log "Events:" (clj->js events))
;;   (when (and dom-node events)  ; Guard against nil
;;     (let [dataset (new DataSet (format-events-for-vis events))
;;           options #js {:editable true
;;                       :onAdd #(let [item (js->clj % :keywordize-keys true)]
;;                                (go (when-let [saved (<! (save-event! item))]
;;                                      (.update @timeline-data (clj->js saved)))))
;;                       :onMove #(let [item (js->clj % :keywordize-keys true)]
;;                                (go (when-let [saved (<! (update-event! item))]
;;                                      (js/console.log "Updated event:" (clj->js (:event saved)))
;;                                      (.update @timeline-data (clj->js (:event saved))))))
;;                       :onRemove #(let [item (js->clj % :keywordize-keys true)]
;;                                    (go (http/delete (str "/api/events/" (:id item)))
;;                                        (.remove @timeline-data (:id item))))}
;;           timeline (new Timeline dom-node dataset options)]
;;       (reset! timeline-state timeline)
;;       (reset! timeline-data dataset))))

;; (defn timeline-view []
;;   (js/console.log "TIMELINE VIEW")
;;   (let [timeline-ref (atom nil)]
;;     (r/create-class
;;       {:component-did-mount
;;        (fn [_]
;;          (js/console.log "Component mounted, ref is:" @timeline-ref)
;;          (go (when-let [events (<! (fetch-events))]
;;                (js/console.log "Got events:" (clj->js events))
;;                (when-let [node (.-firstElementChild @timeline-ref)]
;;                  (init-timeline! node events)))))

;;        :reagent-render
;;        (fn []
;;          [:div {:ref #(reset! timeline-ref %)
;;                 :style {:height "400px"}}
;;           [:div]])})))  ; Add inner div for timeline to attach to
