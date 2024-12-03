(ns rlc.lightcone.views.event-list
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [re-com.core :as rc]))


(defn event-list []
  (let [events @(rf/subscribe [:events])]
    [:div.event-list
     [:h3 "Events"]
     (js/console.log "Rendering event list with:" (clj->js events))
     (for [event events]
       ^{:key (:id event)}
       [:div.event {:style {:margin "10px"
                           :padding "10px"
                           :border "1px solid #ccc"}}
        [:h4 (:title event)]
        [:div "Time: " (:time event)]
        [:div "Participants: " (count (:participants event))]
        (when (seq (:participants event))
          [:ul
           (for [participant (:participants event)]
             ^{:key participant}
             [:li participant])])

        ])]))
