(ns rlc.lightcone.views.timeline
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            ["vis-timeline/standalone" :refer [Timeline DataSet]]
            [re-com.core :as rc]))


(defn format-events-for-vis [events]
  (js/console.log "Formatting events for vis" (clj->js events))
  (js/console.log "test" (js/Date. (:time (first events))))
  (clj->js (map (fn [event]
                  {:id (:uid event)
                   :content (:title event)
                   :type "point"
                   :start (js/Date. (:time event))  ;; Convert string to JS Date
                   ;; :end (js/Date. (:end event))
                   })    ;; Convert string to JS Date
                (or events []))))

(defn timeline-component []
  (r/with-let [timeline-ref (atom nil)
               timeline-instance (atom nil)
               dataset (atom nil)
               events (rf/subscribe [:events])
               _ (add-watch events :timeline
                          (fn [_ _ old-events new-events]
                            (when (and @dataset
                                     (not= old-events new-events)
                                     (seq new-events))
                              (js/console.log "Events changed, updating timeline")
                              (.clear @dataset)
                              (.add @dataset (format-events-for-vis new-events)))))]
    (r/create-class
     {:component-did-mount
      (fn [_]
        (js/console.log "Timeline mounted")
        (when-let [node (.-firstElementChild @timeline-ref)]
          (js/console.log "Creating timeline with node:" node)
          (let [new-dataset (new DataSet #js[])
                options #js {:editable true
                           :onAdd #(rf/dispatch [:calendar/save-event (js->clj % :keywordize-keys true)])
                           :onMove #(let [evt (js->clj % :keywordize-keys true)]
                                     (rf/dispatch [:calendar/update-event
                                                  (assoc evt
                                                        :uid (:id evt)
                                                        :time (.-start %))]))
                           :onRemove #(rf/dispatch [:calendar/delete-event (.-id %)])
                           :onUpdate #(rf/dispatch [:calendar/begin-edit-event  (.-id %)])}
                new-timeline (new Timeline node new-dataset options)]
            (reset! dataset new-dataset)
            (reset! timeline-instance new-timeline))))

      :component-will-unmount
      (fn [_]
        (remove-watch events :timeline))

      :reagent-render
      (fn []
        [:div {:ref #(reset! timeline-ref %)
               :style {:height "400px"}}
         [:div]])})))
